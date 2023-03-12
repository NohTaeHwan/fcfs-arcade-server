package com.josh.toy.fcfsarcade.arcade.repository;

import com.josh.toy.fcfsarcade.arcade.entity.Arcade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ArcadeRepository extends JpaRepository<Arcade,Long> {

    @Modifying
    @Query(value = "update arcade set arcade_status = 0,queue_name = :queueName  where id = :arcadeId and arcade_status = 0",nativeQuery = true)
    void startArcade(@Param(value = "arcadeId") Long arcadeId,@Param(value = "queueName")String queueName);

    /*@Modifying
    @Query(value = "update arcade set arcade_status = 2 where arcade_id = :#{arcadeId} and arcade_statue = 1",nativeQuery = true)
    void endArcade(Long arcadeId);*/


}
