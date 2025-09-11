package com.projeto.quadrokanban.adapter.output.repository;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import com.projeto.quadrokanban.adapter.output.entity.TaskEntity;
import com.projeto.quadrokanban.adapter.output.mapper.TaskMapper;
import com.projeto.quadrokanban.core.domain.model.Task;
import com.projeto.quadrokanban.core.port.output.TaskOutputPort;


@Repository
public class TaskRepository implements TaskOutputPort{
	
	private final JdbcTemplate jdbcTemplate;
	private final TaskMapper taskMapper;
	private final BeanPropertyRowMapper<TaskEntity> rowMapper = new BeanPropertyRowMapper<>(TaskEntity.class);
	
	public TaskRepository (JdbcTemplate jdbcTemplate, TaskMapper taskMapper) {
		this.jdbcTemplate = jdbcTemplate;
		this.taskMapper = taskMapper;
	}
	
	@Override
	public List<Task> findAll() {
		String sql = "SELECT * FROM tb_task";
		List<TaskEntity> entities = jdbcTemplate.query(sql, rowMapper);
		// Usa o mapper para converter a lista de entidades para uma lista de domínio
		return entities.stream()
				.map(taskMapper::toDomain)
				.collect(Collectors.toList());
	}

	@Override
	public Optional<Task> findById(Long id) {
		String sql = "SELECT * FROM get_task_with_id(?)";
		// Encontra a entidade e, se existir, a converte para o domínio
		return jdbcTemplate.query(sql, rowMapper, id)
				.stream()
				.findFirst()
				.map(taskMapper::toDomain);
	}

	@Override
	public List<Task> findAllByTitleContainingIgnoreCase(String title) {
		String sql = "SELECT * FROM tb_task WHERE LOWER(title) LIKE LOWER (?)";
		List<TaskEntity> entities = jdbcTemplate.query(sql, rowMapper, "%" + title + "%");
		return entities.stream().map(taskMapper::toDomain).collect(Collectors.toList());
	}

	@Override
	public Task save(Task task) {
		TaskEntity taskEntity = taskMapper.toEntity(task);
		
		if (taskEntity.getId() == null) {
			// Insert
			String sql = "INSERT INTO pr_upsert_task";
			KeyHolder keyHolder = new GeneratedKeyHolder();
			
			
			jdbcTemplate.update(connection -> {
	            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
	            ps.setString(1, taskEntity.getTitle());
	            ps.setString(2, taskEntity.getDescription());
	            ps.setString(3, taskEntity.getStatus().name());
	            ps.setLong(4, taskEntity.getBoardId());
	            return ps;
	        }, keyHolder);
			
			taskEntity.setId(keyHolder.getKey().longValue());
		} else {
			//Update
			String sql = "UPDATE tb_task SET title = ?, description = ?, updated_at = ?, status = ?, board_id = ? WHERE id = ?";
			jdbcTemplate.update(sql, 
					taskEntity.getTitle(), 
					taskEntity.getDescription(), 
					java.sql.Timestamp.valueOf(java.time.LocalDateTime.now()),
					taskEntity.getStatus().name(), 
					taskEntity.getBoardId(), 
					taskEntity.getId());

			
		};
		return taskMapper.toDomain(taskEntity);
	}

	@Override
	public void deleteById(Long id) {
		String sql = "DELETE FROM tb_task WHERE id = ?";
		jdbcTemplate.update(sql, id);
		
	}


}
