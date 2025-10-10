package com.projeto.quadrokanban.factory;

import com.projeto.quadrokanban.core.domain.model.Board;
import com.projeto.quadrokanban.core.enums.BoardStatus;

import java.util.ArrayList;

public class BoardFactoryBot {

    public static Board createdBoard(){
        return new Board(1L, "Teste Board", BoardStatus.ACTIVE, new ArrayList<>());
    }

    public static Board updateBoard(){
        return new Board(1L, "Board Atualizado", BoardStatus.ACTIVE, new ArrayList<>());
    }

    public static Board validBoard(){
        return new Board(null, "Teste Board", BoardStatus.ACTIVE, new ArrayList<>());
    }

}
