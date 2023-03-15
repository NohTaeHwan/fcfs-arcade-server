package com.josh.toy.fcfsarcade.scheduler;

import com.josh.toy.fcfsarcade.arcade.entity.Arcade;
import com.josh.toy.fcfsarcade.arcade.entity.ArcadeWinner;
import com.josh.toy.fcfsarcade.arcade.entity.User;
import com.josh.toy.fcfsarcade.arcade.repository.ArcadeRepository;
import com.josh.toy.fcfsarcade.arcade.repository.ArcadeWinnerRepository;
import com.josh.toy.fcfsarcade.arcade.repository.UserRepository;
import com.josh.toy.fcfsarcade.common.exception.EntityNotFoundException;
import com.josh.toy.fcfsarcade.common.exception.RedisZSetNullException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.ScheduledFuture;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class PopScheduler {

    private final TaskScheduler taskScheduler;
    private ScheduledFuture<?> scheduledFuture;
    private final RedisTemplate<String,Long> arcadeRedisTemplate;

    @Value("${scheduler.process-period}")
    private String PROCESS_PERIOD;

    @Value("${scheduler.process-size}")
    private String PROCESS_SIZE;

    private final ArcadeWinnerRepository arcadeWinnerRepository;
    private final ArcadeRepository arcadeRepository;

    private final UserRepository userRepository;


    /**
     *
     * TODO 큐에서 데이터 가져오는 스케쥴링 로직 완성 + 테스트
     */
    public void startScheduling(Long arcadeId) {

        log.info("[={}] Start Scheduling... ",arcadeId);
        // TODO scheduledFuture를 Map에 넣어서 multiple Queue 처리 필요
        // TODO save()중일때 getWinnerCount에 대한 동시성 이슈 처리.
        // TODO 스케쥴러 종료 조건 (갯수 비교로 새로운 조건)
        scheduledFuture = taskScheduler.scheduleAtFixedRate(
                ()->{

                    Arcade arcade = arcadeRepository.findById(arcadeId).orElseThrow(EntityNotFoundException::new);

                    /* QueueName or Id 으로 검색해서 winCount == winner_count 비교 */
                    if (arcade.getWinCount().equals(arcadeWinnerRepository.getWinnerCount(arcadeId))){

                        log.info("Stopping scheduling...");
                        // TODO 아케이드 db 종료 처리
                        scheduledFuture.cancel(true);
                    }


                    if(!scheduledFuture.isCancelled()){
                        Set<ZSetOperations.TypedTuple<Long>> userIdSet = arcadeRedisTemplate.opsForZSet().popMin(arcade.getQueueName(),Integer.parseInt(PROCESS_SIZE));

                        /* redis zSet data null check */
                        if(userIdSet == null){
                            throw new RedisZSetNullException();
                        }

                        List<ArcadeWinner> insertDataList = userIdSet.stream().map(element->{
                            Object redisValue = element.getValue();
                            Long userId = ((Integer)redisValue).longValue();

                            User user = userRepository.findById(userId).orElseThrow(EntityNotFoundException::new);

                            return ArcadeWinner.builder()
                                    .arcade(arcade)
                                    .user(user)
                                    .winDate(LocalDateTime.now())
                                    .applyDate(LocalDateTime.ofInstant(Instant.ofEpochMilli(element.getScore().longValue()), TimeZone.getDefault().toZoneId()))
                                    .build();
                        }).collect(Collectors.toList());


                        /* flush까지 해서 in-memory가 아닌 db에 바로 변경이 적용되도록 설정 */
                        // NOTE 다만 매번 flush를 함으로써 생기는 성능 저하를 고려해야함.
                        arcadeWinnerRepository.saveAllAndFlush(insertDataList);
                        log.info("save complete.. {}",insertDataList);

                    }
                },Integer.parseInt(PROCESS_PERIOD));
    }

    public void stopScheduling(){
        if(!scheduledFuture.isCancelled()){
            scheduledFuture.cancel(true);

            log.info("Stopping scheduling...");

        }

    }


}
