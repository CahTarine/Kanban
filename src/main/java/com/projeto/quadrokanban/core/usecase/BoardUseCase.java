package com.projeto.quadrokanban.core.usecase;

import java.util.List;
import java.util.Optional;

import com.projeto.quadrokanban.core.domain.exception.InvalidStatusException;
import com.projeto.quadrokanban.util.validation.BoardValidatorService;
import org.springframework.stereotype.Service;

import com.projeto.quadrokanban.core.domain.exception.BoardValidationException;
import com.projeto.quadrokanban.core.domain.model.Board;
import com.projeto.quadrokanban.core.enums.BoardStatus;
import com.projeto.quadrokanban.core.port.input.BoardInputPort;
import com.projeto.quadrokanban.core.port.output.BoardOutputPort;

@Service
public class BoardUseCase implements BoardInputPort{
	
	private final BoardOutputPort boardOutputPort;
    private final BoardValidatorService boardValidatorService;

    public BoardUseCase(BoardOutputPort boardOutputPort, BoardValidatorService boardValidatorService) {
         this.boardOutputPort = boardOutputPort;
         this.boardValidatorService = boardValidatorService;
	    }
	 
	 public List<Board> getAllBoards() {
	        return boardOutputPort.findAll();
	    }
	 
	 public Board getById(Long id){
         return boardValidatorService.validateBoardExists(id);
	 }

	 public List<Board> getByName(String name){
		 return boardOutputPort.findAllByNameContainingIgnoreCase(name);
	 }
	 
	 public Board createdBoard(Board board) {
		 return boardOutputPort.save(board);
	 }
	 
	 
	 public Board updateBoard(Long id, Board boardUpdates) {
	       Board existingBoard = boardValidatorService.validateBoardExists(id);
	       existingBoard.setName(boardUpdates.getName());
	       existingBoard.setStatus(boardUpdates.getStatus());
	       
	       return boardOutputPort.save(existingBoard);
	 
	 }

	    public void deleteBoard(Long id) {
	        boardValidatorService.validateBoardExists(id);
         boardOutputPort.deleteById(id);
	    }

	    public Optional<Long> countTasks(Long boardId){
	    	boardValidatorService.validateBoardExists(boardId);
         return boardOutputPort.countTasksByBoard(boardId);
	    }
	    
	    public List<Board> getBoadsWithOverdueTasks(){
	    	return boardOutputPort.findBoadsWithOverdueTasks();
	    }

	    public List<Board> getByStatus(String status){
            try {
                BoardStatus boardStatus = BoardStatus.valueOf(status.toUpperCase());
                return boardOutputPort.findByStatus(boardStatus);
            } catch (IllegalArgumentException e) {
                throw new InvalidStatusException("Invalid status.");
            }
	    }
	    
	    @Override
	    public void finalizedBoard(Long boardId) {
            boardValidatorService.validateBoardExists(boardId);
	        boolean allTasksDone = boardOutputPort.areAllTasksDone(boardId); // Verifica se todas as tasks estão concluídas
	        
	        if (allTasksDone) {
	            boardOutputPort.updateBoardStatus(boardId, BoardStatus.COMPLETED);
	        } else {
	            throw new BoardValidationException("Cannot finalize board with pending tasks.");
	        }
	    }
}
