package com.projeto.quadrokanban.adapter.output.entity;

import lombok.Data;

@Data
public class BoardEntity {

    private Long id;
    private String name;
    
    
    public Long getId() {
		return id;
	}


	public void setId(Long id) {
		this.id = id;
	}
}
