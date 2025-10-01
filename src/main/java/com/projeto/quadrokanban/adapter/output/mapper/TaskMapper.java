package com.projeto.quadrokanban.adapter.output.mapper;

import com.projeto.quadrokanban.adapter.output.entity.TaskEntity;
import com.projeto.quadrokanban.core.domain.model.Board;
import com.projeto.quadrokanban.core.domain.model.Task;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TaskMapper {
	
	@Mapping(source = "boardId", target = "board")
    @Mapping(source = "userId", target = "userId")
    @Mapping(source = "dueDate", target = "dueDate")
	Task toDomain(TaskEntity taskEntity);
	
	@Mapping(source = "board.id", target = "boardId")
    @Mapping(source = "userId", target = "userId")
    @Mapping(source = "dueDate", target = "dueDate")
	TaskEntity toEntity(Task task);
	
	// Metodo para converter Long boardId para Board
	default Board mapBoardIdToBoard(Long boardId) {
        if (boardId == null) {
            return null;
        }
        Board board = new Board();
        board.setId(boardId);
        return board;
    }
}