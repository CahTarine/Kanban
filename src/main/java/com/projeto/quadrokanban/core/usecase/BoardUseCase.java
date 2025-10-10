package com.projeto.quadrokanban.core.usecase;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.projeto.quadrokanban.core.domain.exception.BoardNotFoundException;
import com.projeto.quadrokanban.core.domain.exception.BoardValidationException;
import com.projeto.quadrokanban.core.domain.model.Board;
import com.projeto.quadrokanban.core.enums.BoardStatus;
import com.projeto.quadrokanban.core.port.input.BoardInputPort;
import com.projeto.quadrokanban.core.port.output.BoardOutputPort;

@Service
public class BoardUseCase implements BoardInputPort{
	
	private final BoardOutputPort boardOutputPort;
	
	 public BoardUseCase(BoardOutputPort boardOutputPort) {
	        this.boardOutputPort = boardOutputPort;
	    }
	 
	 public List<Board> getAllBoards() {
	        return boardOutputPort.findAll();
	    }
	 
	 public Optional<Board> getById(Long id){
		 return boardOutputPort.findById(id);
	 }

	 public List<Board> getByName(String name){
		 return boardOutputPort.findAllByNameContainingIgnoreCase(name);
	 }
	 
	 public Board createdBoard(Board board) {
		 return boardOutputPort.save(board);
	 }
	 
	 
	 public Board updateBoard(Long id, Board boardUpdates) {
	       Board existingBoard = boardOutputPort.findById(id)
	    		   .orElseThrow(() -> new BoardNotFoundException("Board not found with ID: " + id));
	       existingBoard.setName(boardUpdates.getName());
	       existingBoard.setStatus(boardUpdates.getStatus());
	       
	       return boardOutputPort.save(existingBoard);
	 
	 }

	    public void deleteBoard(Long id) {
	        boardOutputPort.deleteById(id);
	    }
	    
	    public boolean existsById(Long id) {
	        return boardOutputPort.findById(id).isPresent();
	    }
	    
	    public Optional<Long> countTasks(Long boardId){
	    	return boardOutputPort.countTasksByBoard(boardId);
	    }
	    
	    public List<Board> getBoadsWithOverdueTasks(){
	    	return boardOutputPort.findBoadsWithOverdueTasks();
	    }
	    
	    public List<Board> getByStatus(BoardStatus status){
	    	return boardOutputPort.findByStatus(status);
	    }
	    
	    @Override
	    public void finalizedBoard(Long boardId) {
	        boolean allTasksDone = boardOutputPort.areAllTasksDone(boardId); // Verifica se todas as tasks estão concluídas
	        
	        if (allTasksDone) {
	            boardOutputPort.updateBoardStatus(boardId, BoardStatus.COMPLETED);
	        } else {
	            throw new BoardValidationException("Cannot finalize board with pending tasks.");
	        }
	    }
}
