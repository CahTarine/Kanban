package com.projeto.quadrokanban.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import com.projeto.quadrokanban.models.Task;

public interface TaskRepository extends JpaRepository<Task, Long>{
	
	public List<Task> findAllByTitleContainingIgnoreCase(@Param ("title") String title);

}
