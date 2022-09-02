package com.goodspartner.security;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "users_id_sequence")
    @SequenceGenerator(name = "users_id_sequence", sequenceName = "users_id_sequence")
    @Column(name = "id")
    private int id;

    @Column(name = "user_name")
    private String userName;

    @Column(name = "email")
    private String email;

    @Column(name = "role")
    private String role;

    @Column(name = "enabled")
    private boolean enabled = false;

}
