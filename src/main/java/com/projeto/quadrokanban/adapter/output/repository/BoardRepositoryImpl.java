package com.projeto.quadrokanban.adapter.output.repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.projeto.quadrokanban.core.domain.model.Board;
import com.projeto.quadrokanban.core.port.output.BoardOutputPort;

@Repository
public class BoardRepositoryImpl implements BoardOutputPort{

	private final JdbcTemplate jdbcTemplate;
	
	public BoardRepositoryImpl(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	
	private Board mapRowToBoard(java.sql.ResultSet rs, int rowNum) throws java.sql.SQLException {
        Board board = new Board();
        board.setId(rs.getLong("id"));
        board.setName(rs.getString("name"));
        return board;
    }
	
	
	@Override
	public List<Board> findAll() {
		String sql = "SELECT * FROM tb_board";
		return jdbcTemplate.query(sql, this::mapRowToBoard);
	}

	@Override
	public Optional<Board> findById(Long id) {
		String sql = "SELECT * FROM tb_board WHERE id = ?";
		return jdbcTemplate.query(sql, this::mapRowToBoard, id).stream().findFirst();
	}

	@Override
	public List<Board> findAllByNameContainingIgnoreCase(String name) {
		String sql = "SELECT * FROM tb_board WHERE LOWER(name) LIKE LOWER(?)";
		return jdbcTemplate.query(sql, this::mapRowToBoard, "%" + name + "%");
	}

	@Override
	public Board save(Board board) {
		if (board.getId() == null) {
			//Insert
			String sql = "INSERT INTO tb_board (name) VALUES (?)";
			
			KeyHolder keyHolder = new GeneratedKeyHolder();
			
			jdbcTemplate.update(connection -> {
	            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
	            ps.setString(1, board.getName());
	            return ps;
			}, keyHolder);
			
			//Ultimo id gerado
			board.setId(keyHolder.getKey().longValue());
		} else {
			//Update
			String sql = "UPDATE tb_board SET name = ? WHERE id = ?";
			jdbcTemplate.update(sql, board.getName(), board.getId());
		}
		return board;
	}

	@Override
	public void deleteById(Long id) {
		String sql = "DELETE FROM tb_board WHERE id = ?";
		jdbcTemplate.update(sql, id);
		
	}



}
