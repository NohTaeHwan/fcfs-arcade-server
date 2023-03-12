package com.josh.toy.fcfsarcade.arcade.repository;

import com.josh.toy.fcfsarcade.arcade.entity.ArcadeWinner;
import org.hibernate.annotations.Formula;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ArcadeWinnerRepository extends JpaRepository<ArcadeWinner,Long> {

    @Query(value = "select count(*) from arcade_winner where arcade_id = :arcadeId",nativeQuery = true)
    Integer getWinnerCount(@Param("arcadeId") Long arcadeId);

    boolean existsArcadeWinnerByUserId(Long userId);
}
