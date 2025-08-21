package com.projeto.quadrokanban.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import com.projeto.quadrokanban.models.Board;

public interface BoardRepository extends JpaRepository<Board, Long>{
	
	public List<Board> findAllByNameContainingIgnoreCase(@Param ("name") String name);

}
