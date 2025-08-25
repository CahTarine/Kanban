package com.projeto.quadrokanban.core.domain.model;

import java.util.ArrayList;
import java.util.List;

public class Board {
	
	private String name;
	private List<Task> tasks = new ArrayList<>();
	
	
	public Board(String name, List<Task> tasks) {
		super();
		this.name = name;
		this.tasks = tasks;
	}


	public Board() {
		super();
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
	
	
	

}
