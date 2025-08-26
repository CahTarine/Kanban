package com.projeto.quadrokanban.adapter.output.repository;

import java.util.List;
import java.util.Optional;

import com.projeto.quadrokanban.adapter.output.entity.BoardEntity;
import com.projeto.quadrokanban.adapter.output.mapper.BoardMapper;
import com.projeto.quadrokanban.core.domain.model.Board;
import com.projeto.quadrokanban.core.port.output.BoardOutputPort;


public class BoardRepositoryImpl implements BoardOutputPort{

	private final BoardRepository boardRepository;
	private final BoardMapper boardMapper;
	
	public BoardRepositoryImpl(BoardRepository boardRepository, BoardMapper boardMapper) {
		this.boardRepository = boardRepository;
		this.boardMapper = boardMapper;
	}
	
	
	
	@Override
	public List<Board> findAll() {
		return boardMapper.toDomainList(boardRepository.findAll());
	}

	@Override
	public Optional<Board> findById(Long id) {
		return boardRepository.findById(id).map(boardMapper::toDomain);
	}

	@Override
	public List<Board> findAllByNameContainingIgnoreCase(String name) {
		return boardMapper.toDomainList(boardRepository.findAllByNameContainingIgnoreCase(name));
	}

	@Override
	public Board save(Board board) {
		BoardEntity entity = boardMapper.toEntity(board);
		BoardEntity saved = boardRepository.save(entity);
		return boardMapper.toDomain(saved);
	}

	@Override
	public void deleteById(Long id) {
		this.boardRepository.deleteById(id);
		
	}



}
