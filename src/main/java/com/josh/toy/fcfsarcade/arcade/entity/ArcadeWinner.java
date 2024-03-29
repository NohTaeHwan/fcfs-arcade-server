package com.josh.toy.fcfsarcade.arcade.entity;


import lombok.*;
import org.hibernate.annotations.Formula;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@EqualsAndHashCode(of="id")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name="arcade_winner")
public class ArcadeWinner {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "arcade_id")
    private Arcade arcade;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private LocalDateTime winDate;

    private LocalDateTime applyDate;


}
