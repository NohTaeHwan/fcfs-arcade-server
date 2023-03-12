package com.josh.toy.fcfsarcade.arcade.service;

import com.josh.toy.fcfsarcade.arcade.entity.Arcade;
import com.josh.toy.fcfsarcade.arcade.entity.ArcadeWinner;
import com.josh.toy.fcfsarcade.arcade.entity.User;
import com.josh.toy.fcfsarcade.arcade.repository.ArcadeRepository;
import com.josh.toy.fcfsarcade.arcade.repository.ArcadeWinnerRepository;
import com.josh.toy.fcfsarcade.arcade.repository.UserRepository;
import com.josh.toy.fcfsarcade.common.exception.ArcadeException;
import com.josh.toy.fcfsarcade.common.exception.BusinessException;
import com.josh.toy.fcfsarcade.common.exception.EntityNotFoundException;
import com.josh.toy.fcfsarcade.common.exception.ErrorCode;
import com.josh.toy.fcfsarcade.scheduler.PopScheduler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class ArcadeService {

    private final RedisTemplate<String,Long> arcadeRedisTemplate;

    /*@Value("${scheduler.process-interval}")
    private final String PROCESS_INTERVAL;*/

   /* @Value("${scheduler.process-data}")
    private final String PROCESS_DATA;*/

    private final ArcadeRepository arcadeRepository;

    private final ArcadeWinnerRepository arcadeWinnerRepository;

    private final UserRepository userRepository;


    private final Map<String, ScheduledFuture<?>> scheduledTasks = new ConcurrentHashMap<>();
    private final TaskScheduler taskScheduler;
    private final PopScheduler popScheduler;

    @Transactional
    public void openArcade(Long arcadeId){
        /* VALIDATION */
        Arcade arcade = arcadeRepository.findById(arcadeId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.INTERNAL_SERVER_ERROR.value()));

        if(arcade.getArcadeStatus() == 1 ||arcade.getArcadeStatus() == 2){
            // 아케이드가 시작전 상태가아니라면 throw
            throw new ArcadeException();
        }

        /* BUSINESS LOGIC */
        String queueName = "WAIT_QUEUE_"+arcadeId.hashCode();

        // 아케이드 시작
        arcadeRepository.startArcade(arcade.getId(),queueName);

        // 큐 조회 스케쥴러 시작
        popScheduler.startScheduling(arcadeId);

    }

    public void playArcade(Long userId,Long arcadeId){

        /* VALIDATION */
        User user = userRepository.findById(userId).orElseThrow(EntityNotFoundException::new);

        Arcade arcade = arcadeRepository.findById(arcadeId).orElseThrow(EntityNotFoundException::new);

        /* BUSINESS LOGIC */
        if(arcadeWinnerRepository.existsArcadeWinnerByUserId(userId)){
            throw new ArcadeException(ErrorCode.DUPLICATE_PLAY.value());
        }

        Double score = arcadeRedisTemplate.opsForZSet().score(arcade.getQueueName(),userId);
        if(score==null){
            arcadeRedisTemplate.opsForZSet().add(arcade.getQueueName(),userId,System.currentTimeMillis());
        }else{
            throw new ArcadeException(ErrorCode.DUPLICATE_PLAY.value());
        }

    }


}
