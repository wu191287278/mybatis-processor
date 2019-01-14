package com.vcg.mybatis.example.starter;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Table(name = "user")
public class User {

    @Id
    @GeneratedValue
    private Integer id;

    @Column(name = "username", columnDefinition = "VARCHAR")
    private String username;

    @Column(name = "password", columnDefinition = "VARCHAR")
    private String password;

}
