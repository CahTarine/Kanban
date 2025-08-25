package com.projeto.quadrokanban.adapter.output.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import com.projeto.quadrokanban.adapter.output.entity.BoardEntity;

public interface BoardRepository extends JpaRepository<BoardEntity, Long>{
	
	public List<BoardEntity> findAllByNameContainingIgnoreCase(@Param ("name") String name);

}
