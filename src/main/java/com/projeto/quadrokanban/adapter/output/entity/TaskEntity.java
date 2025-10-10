package com.projeto.quadrokanban.adapter.output.entity;

import com.projeto.quadrokanban.core.enums.TaskStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TaskEntity {

	private Long id;
	private String title;
	private String description;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	private TaskStatus status;
	private Long boardId;
	
	public Long getId() {
		return id;
	}


	public void setId(Long id) {
		this.id = id;
	}
}
