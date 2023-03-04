package com.josh.toy.fcfsarcade.arcade.entity;


import lombok.*;

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
}
