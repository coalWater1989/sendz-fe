package com.charbz.model;

import lombok.Data;

import java.sql.Blob;

@Data
public class User {
    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private Blob image;
}
