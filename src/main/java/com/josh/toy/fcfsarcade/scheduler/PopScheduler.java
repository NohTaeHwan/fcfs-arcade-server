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
        scheduledFuture = taskScheduler.scheduleAtFixedRate(
                ()->{
                    Arcade arcade = arcadeRepository.findById(arcadeId).orElseThrow(EntityNotFoundException::new);

                    // QueueName or Id 으로 검색해서 winCount == winner_count 비교
                    if (arcade.getWinCount().equals(arcadeWinnerRepository.getWinnerCount(arcadeId))){
                        System.out.println("Stopping scheduling...");
                        scheduledFuture.cancel(true);
                    }


                    if(!scheduledFuture.isCancelled()){
                        //TODO queue에서 데이터 가져오기 ( popMin()으로 변경 )
                        //arcadeRedisTemplate.opsForZSet().popMin(arcade.getQueueName()).getScore();
                        Set<ZSetOperations.TypedTuple<Long>> queueSet = arcadeRedisTemplate.opsForZSet().popMin(arcade.getQueueName(),5);
                        //Set<?> queueSet = arcadeRedisTemplate.opsForZSet().range(arcade.getQueueName(),0,10);

                        //TODO arcade_winner에 데이터 추가하는 쿼리 + 중복 유저는 추가되면 안됨
                        queueSet.forEach(element->{
                            User user = userRepository.findById(element.getValue()).orElseThrow(EntityNotFoundException::new);
                            //TODO 코드 변경후에는 popMin() 에서 가져온 getScore()로 applyDate 등록
                            ArcadeWinner arcadeWinner = ArcadeWinner.builder()
                                    .arcade(arcade)
                                    .user(user)
                                    .applyDate(LocalDateTime.now())
                                    .winDate(LocalDateTime.now())
                                    .build();

                            arcadeWinnerRepository.save(arcadeWinner);

                        });


                    }
                },2000);
    }


}
