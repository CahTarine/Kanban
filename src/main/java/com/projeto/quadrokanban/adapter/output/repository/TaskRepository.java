package com.projeto.quadrokanban.adapter.output.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import com.projeto.quadrokanban.adapter.output.entity.TaskEntity;

public interface TaskRepository extends JpaRepository<TaskEntity, Long>{
	
	public List<TaskEntity> findAllByTitleContainingIgnoreCase(@Param ("title") String title);

}
