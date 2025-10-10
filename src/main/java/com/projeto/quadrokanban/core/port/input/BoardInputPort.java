package com.projeto.quadrokanban.core.port.input;

import java.util.List;
import java.util.Optional;

import com.projeto.quadrokanban.core.domain.model.Board;
import com.projeto.quadrokanban.core.enums.BoardStatus;
import jdk.jshell.Snippet;

public interface BoardInputPort {
	
	List<Board> getAllBoards();
	
	Board getById(Long id);
	
	List<Board> getByName(String name);
	
	Board createdBoard(Board board);
	
	Board updateBoard(Long id, Board board);
	
	void deleteBoard(Long id);
	
	Optional<Long> countTasks(Long boardId);
	
	List<Board> getBoadsWithOverdueTasks();
	
	List<Board> getByStatus(String status);
	
	void finalizedBoard(Long boardId);

}
