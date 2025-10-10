package com.projeto.quadrokanban.core.usecase;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.projeto.quadrokanban.core.domain.model.Board;
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
	 
	 public Optional<Board> updateBoard(Long id, Board board) {
	        return boardOutputPort.findById(id).map(existing -> {
	            board.setId(id);
	            return boardOutputPort.save(board);
	        });
	    }

	    public void deleteBoard(Long id) {
	        boardOutputPort.deleteById(id);
	    }
	    
	    public boolean existsById(Long id) {
	        return boardOutputPort.findById(id).isPresent();
	    }

}
