package com.projeto.quadrokanban.core.domain.model;

import java.util.ArrayList;
import java.util.List;

import com.projeto.quadrokanban.core.enums.BoardStatus;

public class Board {
	
	private Long id;
	private String name;
	private List<Task> tasks = new ArrayList<>();
	private BoardStatus status;
	
	
	public Board(String name, List<Task> tasks) {
		super();
		this.name = name;
		this.tasks = tasks;
	}


	public Board() {
		super();
	}


	
	public Long getId() {
		return id;
	}


	public void setId(Long id) {
		this.id = id;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public List<Task> getTasks() {
		return tasks;
	}


	public void setTasks(List<Task> tasks) {
		this.tasks = tasks;
	}


	public BoardStatus getStatus() {
		return status;
	}


	public void setStatus(BoardStatus status) {
		this.status = status;
	}
	
	
	

}
