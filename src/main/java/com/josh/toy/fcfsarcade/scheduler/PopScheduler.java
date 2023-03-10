package com.josh.toy.fcfsarcade.scheduler;

import com.josh.toy.fcfsarcade.arcade.entity.Arcade;
import com.josh.toy.fcfsarcade.arcade.entity.ArcadeWinner;
import com.josh.toy.fcfsarcade.arcade.entity.User;
import com.josh.toy.fcfsarcade.arcade.repository.ArcadeRepository;
import com.josh.toy.fcfsarcade.arcade.repository.ArcadeWinnerRepository;
import com.josh.toy.fcfsarcade.arcade.repository.UserRepository;
import com.josh.toy.fcfsarcade.common.exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.concurrent.ScheduledFuture;

@Slf4j
@Component
@RequiredArgsConstructor
public class PopScheduler {

    private final TaskScheduler taskScheduler;
    private ScheduledFuture<?> scheduledFuture;
    private final RedisTemplate<String,Long> arcadeRedisTemplate;

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
        scheduledFuture = taskScheduler.scheduleAtFixedRate(
                ()->{
                    Arcade arcade = arcadeRepository.findById(arcadeId).orElseThrow(EntityNotFoundException::new);

                    /* QueueName or Id 으로 검색해서 winCount == winner_count 비교 */
                    // FIXME 이러면 save()중일때 getWinnerCount가 접근되어서 동시성에 문제가 생길거 같음
                    if (arcade.getWinCount().equals(arcadeWinnerRepository.getWinnerCount(arcadeId))){

                        log.info("Stopping scheduling...");
                        scheduledFuture.cancel(true);
                    }


                    if(!scheduledFuture.isCancelled()){
                        //TODO queue에서 데이터 가져오는 부분 상수로 처리
                        //Set<ZSetOperations.TypedTuple<Long>> queueSet = arcadeRedisTemplate.opsForZSet().popMin(arcade.getQueueName(),5);
                        // TODO popMin으로 변경해서 처리
                        Set<ZSetOperations.TypedTuple<Long>> queueSet = arcadeRedisTemplate.opsForZSet().rangeByScoreWithScores(arcade.getQueueName(),0,5);

                        //TODO TypedTuple null 체크
                        queueSet.forEach(element->{

                            // redis ZSet에서 값 조회할때 정수를 Integer로 인식하는 이슈때문에 캐스팅 작업 필요
                            Object redisValue = element.getValue();
                            Long userId = ((Integer)redisValue).longValue();

                            User user = userRepository.findById(userId).orElseThrow(EntityNotFoundException::new);

                            //TODO 코드 변경후에는 popMin() 에서 가져온 getScore()로 applyDate 등록
                            ArcadeWinner arcadeWinner = ArcadeWinner.builder()
                                    .arcade(arcade)
                                    .user(user)
                                    .winDate(LocalDateTime.now())
                                    .applyDate(LocalDateTime.now())
                                    .build();


                            ArcadeWinner completeEntity =  arcadeWinnerRepository.save(arcadeWinner);
                            log.info("save complete.. {}",completeEntity);
                        });


                    }
                },3000);
    }


}
