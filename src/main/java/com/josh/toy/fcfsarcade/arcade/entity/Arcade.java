package com.josh.toy.fcfsarcade.arcade.entity;


import lombok.*;
import org.hibernate.annotations.Formula;

import javax.persistence.*;

@Entity
@Getter
@EqualsAndHashCode(of="id")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name="arcade")
public class Arcade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String arcadeTitle;

    private Integer arcadeStatus;

    private Integer winCount;

    private String queueName;

    @Formula("(SELECT count(1) FROM arcade_winner aw WHERE aw.arcade_id = id)")
    private Integer currentWinnerCount;
}
