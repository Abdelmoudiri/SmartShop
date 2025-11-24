package com.SmartShop.SmartShop.entities.enums;



public class Client {


    @Id
    private UUID id;

    private String name;

    private String email;
    
    @Enumerated(EnumType.STRING)
    private Niveu niveu;

}
