package com.projeto.quadrokanban.adapter.output.entity;

import com.projeto.quadrokanban.core.enums.BoardStatus;

import lombok.Data;

@Data
public class BoardEntity {

    private Long id;
    private String name;
    private BoardStatus status;
    
    
    public Long getId() {
		return id;
	}


	public void setId(Long id) {
		this.id = id;
	}
	
	public BoardEntity() {
        this.status = BoardStatus.ACTIVE;
    }
}
