package com.SmartShop.SmartShop.entities;


public class User{


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    priva String username;

    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

}