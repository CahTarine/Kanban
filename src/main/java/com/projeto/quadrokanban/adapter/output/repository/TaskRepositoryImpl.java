package com.projeto.quadrokanban.adapter.output.repository;

import java.util.List;
import java.util.Optional;

import com.projeto.quadrokanban.adapter.output.entity.TaskEntity;
import com.projeto.quadrokanban.adapter.output.mapper.TaskMapper;
import com.projeto.quadrokanban.core.domain.model.Task;
import com.projeto.quadrokanban.core.port.output.TaskOutputPort;

public class TaskRepositoryImpl implements TaskOutputPort{
	
	private final TaskRepository taskRepository;
	private final TaskMapper taskMapper;
	
	public TaskRepositoryImpl (TaskRepository taskRepository, TaskMapper taskMapper) {
		this.taskRepository = taskRepository;
		this.taskMapper = taskMapper;
	}

	
	
	@Override
	public List<Task> findAll() {
		return taskMapper.toDomainList(taskRepository.findAll());
	}

	@Override
	public Optional<Task> findById(Long id) {
		return taskRepository.findById(id).map(taskMapper::toDomain);
	}

	@Override
	public List<Task> findAllByTitleContainingIgnoreCase(String title) {
		return taskMapper.toDomainList(taskRepository.findAllByTitleContainingIgnoreCase(title));
	}

	@Override
	public Task save(Task task) {
		TaskEntity entity = taskMapper.toEntity(task);
		TaskEntity saved = taskRepository.save(entity);
		return taskMapper.toDomain(saved);
	}

	@Override
	public void deleteById(Long id) {
		this.taskRepository.deleteById(id);
		
	}

}
