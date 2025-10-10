package com.projeto.quadrokanban.adapter.output.repository;

import java.sql.CallableStatement;
import java.sql.Types;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.projeto.quadrokanban.adapter.output.entity.BoardEntity;
import com.projeto.quadrokanban.adapter.output.mapper.BoardMapper;
import com.projeto.quadrokanban.core.domain.model.Board;
import com.projeto.quadrokanban.core.enums.BoardStatus;
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
//	Usar .query quando é um método de busca de dados (SELECT)
	public List<Board> findAll() {
		String sql = "SELECT * FROM board.get_all_boards()";
		return jdbcTemplate.query(sql, rowMapper)
				.stream().map(boardMapper::toDomain).collect(Collectors.toList());
	}

	@Override
	public Optional<Board> findById(Long id) {
		String sql = "SELECT * FROM board.get_board_by_id(?)";
		return jdbcTemplate.query(sql,rowMapper, id)
				.stream().findFirst().map(boardMapper::toDomain);
	}

	@Override
	public List<Board> findAllByNameContainingIgnoreCase(String name) {
		String sql = "SELECT * FROM board.get_board_by_name(?)";
		return jdbcTemplate.query(sql, rowMapper, "%" + name + "%")
		.stream().map(boardMapper::toDomain).collect(Collectors.toList());
	}

	@Override
//	Usar .execute quando é um metodo de manipulação de dados ou que possue parametro de entrada e saída (INOUT/OUT)
	public Board save(Board board) {
		BoardEntity boardEntity = boardMapper.toEntity(board);
		
		String sql = "{? = call board.upsert_board(?, ?)}";
		
		if (boardEntity.getId() == null) {
			//Insert
			
			jdbcTemplate.execute(sql, (CallableStatement cs) -> {
				cs.registerOutParameter(1, Types.BIGINT);
				cs.setNull(2, Types.BIGINT);
				cs.setString(3, boardEntity.getName());
				
				cs.execute();
				boardEntity.setId(cs.getLong(1));
				return null;
			});
			
		} else {
			//Update
			jdbcTemplate.execute(sql, (CallableStatement cs) -> {
				cs.registerOutParameter(1, Types.BIGINT);
				cs.setLong(2, boardEntity.getId());
				cs.setString(3, boardEntity.getName());
				
				cs.execute();
				return null;
			});
		}
		return boardMapper.toDomain(boardEntity);
	}

	@Override
	public void deleteById(Long id) {
		String sql = "call pr_delete_board(?)";
		jdbcTemplate.execute(sql, (CallableStatement cs) -> {
			cs.setLong(1, id);
			cs.execute();
			return null;
		
		});
		
	}
	
	@Override
	public Optional<Long> countTasksByBoard(Long boardId){
		String sql = "SELECT * FROM board.count_tasks_by_board(?)";
		
//		Tratamento de erro nulo.
		try {
			Long count = jdbcTemplate.queryForObject(sql, Long.class, boardId); // queryForObject é um metodo que espera 1 unico valor.
			return Optional.ofNullable(count); // Se caso o count for null, vai ser encapsulado em um Optional.empty.
		} catch (EmptyResultDataAccessException e) {
	        return Optional.of(0L); // Optional com valor 0 para garantir que o código nunca retorne um null.
	    }
	}

	
	@Override
	public List<Board> findBoadsWithOverdueTasks(){
		String sql = "SELECT * FROM board.get_boards_with_overdue_tasks()";
		
		return jdbcTemplate.query(sql, rowMapper)
			.stream().map(boardMapper::toDomain).collect(Collectors.toList());
	}
	
	@Override
	public List<Board> findByStatus(BoardStatus status) {
		String sql = "SELECT * FROM board.get_board_by_status(?)";
		
		return jdbcTemplate.query(sql, rowMapper, status.name())
				.stream().map(boardMapper::toDomain).collect(Collectors.toList());
	}


	@Override
    public Boolean areAllTasksDone(Long boardId) {
        String sql = "SELECT board.check_if_board_is_complete(?)";
        return jdbcTemplate.queryForObject(sql, Boolean.class, boardId);
    }
	
	@Override
    public void updateBoardStatus(Long boardId, BoardStatus status) {
        String sql = "call update_board_status(?, ?)";
        jdbcTemplate.update(sql, boardId, status.name());
    }
}
