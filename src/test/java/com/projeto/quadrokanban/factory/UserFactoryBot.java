package com.projeto.quadrokanban.factory;

import com.projeto.quadrokanban.core.domain.model.User;

public class UserFactoryBot {

    public static User createdUser(){
        return new User(1L, "Ana", "ana@email.com");
    }
}
