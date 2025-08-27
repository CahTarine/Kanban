package com.projeto.quadrokanban.adapter.output.repository;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.projeto.quadrokanban.core.domain.model.Board;
import com.projeto.quadrokanban.core.domain.model.Task;
import com.projeto.quadrokanban.core.enums.TaskStatus;
import com.projeto.quadrokanban.core.port.output.TaskOutputPort;



@Repository
public class TaskRepositoryImpl implements TaskOutputPort{
	
	private final JdbcTemplate jdbcTemplate;
	
	public TaskRepositoryImpl (JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	
	private Task mapRowToTask(java.sql.ResultSet rs, int rowNum) throws java.sql.SQLException {
        Task task = new Task();
        task.setId(rs.getLong("id"));
        task.setTitle(rs.getString("title"));
        task.setDescription(rs.getString("description"));
        task.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        task.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
        task.setStatus(TaskStatus.valueOf(rs.getString("status"))); //Valores precisam bater com os salvos no banco
        
        // Busca id do Board
        Board board = new Board();
        board.setId(rs.getLong("board_id")); //FK tb_task
        task.setBoard(board);
        return task;
    }

	
	
	@Override
	public List<Task> findAll() {
		String sql = "SELECT * FROM tb_task";
		return jdbcTemplate.query(sql, this::mapRowToTask);
	}

	@Override
	public Optional<Task> findById(Long id) {
		String sql = "SELECT * FROM tb_task WHERE id = ?";
		return jdbcTemplate.query(sql, this::mapRowToTask, id).stream().findFirst();
	}

	@Override
	public List<Task> findAllByTitleContainingIgnoreCase(String title) {
		String sql = "SELECT * FROM tb_task WHERE LOWER(title) LIKE LOWER (?)";
		return jdbcTemplate.query(sql, this::mapRowToTask, "%" + title + "%");
	}

	@Override
	public Task save(Task task) {
		if (task.getId() == null) {
			String sql = "INSERT INTO tb_task (title, description, status, board_id) VALUES (?, ?, ?, ?)";
			
			KeyHolder keyHolder = new GeneratedKeyHolder();
			
			
			jdbcTemplate.update(connection -> {
	            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
	            ps.setString(1, task.getTitle());
	            ps.setString(2, task.getDescription());
	            ps.setString(3, task.getStatus().name());
	            ps.setLong(4, task.getBoard().getId());
	            return ps;
	        }, keyHolder);
			
			task.setId(keyHolder.getKey().longValue());
		} else {
			String sql = "UPDATE tb_task SET title = ?, description = ?, updated_at = ?, status = ?, board_id = ? WHERE id = ?";
			jdbcTemplate.update(sql, 
					task.getTitle(), 
					task.getDescription(), 
					java.sql.Timestamp.valueOf(java.time.LocalDateTime.now()),
					task.getStatus().name(), 
					task.getBoard().getId(), 
					task.getId());

			
		};
		return task;
	}

	@Override
	public void deleteById(Long id) {
		String sql = "DELETE FROM tb_task WHERE id = ?";
		jdbcTemplate.update(sql, id);
		
	}


}
