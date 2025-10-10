package com.projeto.quadrokanban.core.usecase;

import java.util.List;
import java.util.Optional;

import com.projeto.quadrokanban.core.domain.exception.InvalidStatusException;
import org.springframework.http.ResponseEntity;
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
    private final BoardValidatorService boardValidatorService;

    public BoardUseCase(BoardOutputPort boardOutputPort, BoardValidatorService boardValidatorService) {
         this.boardOutputPort = boardOutputPort;
         this.boardValidatorService = boardValidatorService;
	    }
	 
	 public List<Board> getAllBoards() {
	        return boardOutputPort.findAll();
	    }
	 
	 public Board getById(Long id){
		 return boardOutputPort.findById(id)
                 .orElseThrow(() -> new BoardNotFoundException("Board not found."));
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
	        boardOutputPort.findById(id)
                            .orElseThrow(() -> new BoardNotFoundException("Board not found."));
         boardOutputPort.deleteById(id);
	    }
	    
	    public boolean existsById(Long id) {
	        return boardOutputPort.findById(id).isPresent();
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
            } catch (InvalidStatusException e) {
                throw new InvalidStatusException("Invalid status.");
            }
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
