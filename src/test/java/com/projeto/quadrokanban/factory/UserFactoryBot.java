package com.projeto.quadrokanban.factory;

import com.projeto.quadrokanban.core.domain.model.User;

public class UserFactoryBot {

    public static User createdUser(){
        return new User(1L, "Ana", "ana@email.com");
    }

    public static User validUser(){
        return new User(null,  "Ana", "ana@email.com");
    }

    public static User userWithEmptyName(){
        return new User(null,  "", "ana@email.com");
    }

    public static User updatedUser(){
        return new User(1L, "Ana Clara", "ana.clara@gmail.com");
    }
}
