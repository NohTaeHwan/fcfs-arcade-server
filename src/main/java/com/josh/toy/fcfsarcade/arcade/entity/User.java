package com.josh.toy.fcfsarcade.arcade.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@EqualsAndHashCode(of="id")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name="user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userNick;
}
