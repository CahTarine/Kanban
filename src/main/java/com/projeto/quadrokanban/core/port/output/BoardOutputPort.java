package com.projeto.quadrokanban.core.port.output;

import java.util.List;
import java.util.Optional;

import com.projeto.quadrokanban.core.domain.model.Board;

public interface BoardOutputPort {

    List<Board> findAll();

    Optional<Board> findById(Long id);

    List<Board> findAllByNameContainingIgnoreCase(String name);

    Board save(Board board);

    void deleteById(Long id);
}
