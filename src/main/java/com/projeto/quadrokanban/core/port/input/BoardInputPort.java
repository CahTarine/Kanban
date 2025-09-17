package com.projeto.quadrokanban.core.port.input;

import java.util.List;
import java.util.Optional;

import com.projeto.quadrokanban.core.domain.model.Board;
import com.projeto.quadrokanban.core.enums.BoardStatus;

public interface BoardInputPort {
	
	List<Board> getAllBoards();
	
	Optional<Board> getById(Long id);
	
	List<Board> getByName(String name);
	
	Board createdBoard(Board board);
	
	Optional<Board> updateBoard(Long id, Board board);
	
	void deleteBoard(Long id);
	
	boolean existsById(Long id);
	
	Optional<Long> countTasks(Long boardId);
	
	List<Board> getBoadsWithOverdueTasks();
	
	List<Board> getByStatus(BoardStatus status);
	
	void finalizedBoard(Long boardId);

}
