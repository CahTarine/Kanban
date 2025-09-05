package com.projeto.quadrokanban.adapter.output.repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.projeto.quadrokanban.adapter.output.entity.BoardEntity;
import com.projeto.quadrokanban.adapter.output.mapper.BoardMapper;
import com.projeto.quadrokanban.core.domain.model.Board;
import com.projeto.quadrokanban.core.port.output.BoardOutputPort;

@Repository
public class BoardRepository implements BoardOutputPort{

	private final JdbcTemplate jdbcTemplate;
	private final BoardMapper boardMapper;
	private final BeanPropertyRowMapper<BoardEntity> rowMapper = new BeanPropertyRowMapper<>(BoardEntity.class);
	
	public BoardRepository(JdbcTemplate jdbcTemplate, BoardMapper boardMapper) {
		this.jdbcTemplate = jdbcTemplate;
		this.boardMapper = boardMapper;
	}
	
	
	@Override
	public List<Board> findAll() {
		String sql = "SELECT * FROM tb_board";
		List<BoardEntity> entities = jdbcTemplate.query(sql, rowMapper);
		return entities.stream().map(boardMapper::toDomain).collect(Collectors.toList());
	}

	@Override
	public Optional<Board> findById(Long id) {
		String sql = "SELECT * FROM tb_board WHERE id = ?";
		return jdbcTemplate.query(sql,rowMapper, id).stream().findFirst().map(boardMapper::toDomain);
	}

	@Override
	public List<Board> findAllByNameContainingIgnoreCase(String name) {
		String sql = "SELECT * FROM tb_board WHERE LOWER(name) LIKE LOWER(?)";
		List<BoardEntity> entities = jdbcTemplate.query(sql, rowMapper, "%" + name + "%");
		return entities.stream().map(boardMapper::toDomain).collect(Collectors.toList());
	}

	@Override
	public Board save(Board board) {
		BoardEntity boardEntity = boardMapper.toEntity(board);
		
		if (boardEntity.getId() == null) {
			//Insert
			String sql = "INSERT INTO tb_board (name) VALUES (?)";
			
			KeyHolder keyHolder = new GeneratedKeyHolder();
			
			jdbcTemplate.update(connection -> {
	            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
	            ps.setString(1, boardEntity.getName());
	            return ps;
			}, keyHolder);
			
			//Ultimo id gerado
			boardEntity.setId(keyHolder.getKey().longValue());
		} else {
			//Update
			String sql = "UPDATE tb_board SET name = ? WHERE id = ?";
			jdbcTemplate.update(sql, boardEntity.getName(), boardEntity.getId());
		}
		return boardMapper.toDomain(boardEntity);
	}

	@Override
	public void deleteById(Long id) {
		String sql = "DELETE FROM tb_board WHERE id = ?";
		jdbcTemplate.update(sql, id);
		
	}



}
