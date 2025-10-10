package com.projeto.quadrokanban.util.validation;

import com.projeto.quadrokanban.core.domain.exception.BoardNotFoundException;
import org.springframework.stereotype.Service;

import com.projeto.quadrokanban.core.domain.model.Board;
import com.projeto.quadrokanban.core.port.output.BoardOutputPort;

@Service
public class BoardValidatorService {
    private final BoardOutputPort boardOutputPort;

    public BoardValidatorService(BoardOutputPort boardOutputPort) {
        this.boardOutputPort = boardOutputPort;
    }

    public Board validateBoardExists(Long boardId) {
        return boardOutputPort.findById(boardId)
            .orElseThrow(() -> new BoardNotFoundException("Board does not exist"));
    }
}
