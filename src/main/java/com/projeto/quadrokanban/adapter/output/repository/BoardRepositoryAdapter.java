package com.projeto.quadrokanban.adapter.output.repository;

import java.util.List;
import java.util.Optional;

import com.projeto.quadrokanban.adapter.output.entity.BoardEntity;
import com.projeto.quadrokanban.adapter.output.mapper.BoardMapper;
import com.projeto.quadrokanban.core.domain.model.Board;
import com.projeto.quadrokanban.core.port.output.BoardOutputPort;


public class BoardRepositoryAdapter implements BoardOutputPort {

    private BoardRepository repository;
    private BoardMapper mapper;

    @Override
    public Board save(Board board) {
        return mapper.toDomain(repository.save(mapper.toEntity(board)));
    }

    @Override
    public Optional<Board> findById(Long id) {
        return repository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Board> findAll() {
        return repository.findAll().stream().map(mapper::toDomain).toList();
    }
    
    @Override
    public List<Board> findAllByNameContainingIgnoreCase(String name) {
        List<BoardEntity> entities = repository.findAllByNameContainingIgnoreCase(name);
        return entities.stream()
                       .map(mapper::toDomain) 
                       .toList();
    }
    
    @Override
    public void deleteById(Long id) {
        repository.deleteById(id);
    }

}
