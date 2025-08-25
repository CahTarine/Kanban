package com.projeto.quadrokanban.adapter.output.mapper;

import java.util.List;

import org.mapstruct.Mapper;

import com.projeto.quadrokanban.adapter.output.entity.BoardEntity;
import com.projeto.quadrokanban.core.domain.model.Board;

@Mapper(componentModel = "spring", uses = TaskMapper.class)
public interface BoardMapper {

	Board toDomain(BoardEntity boardEntity);
	BoardEntity toEntity(Board board);
	
	List<Board> toDomainList(List<BoardEntity> entities);
    List<BoardEntity> toEntityList(List<Board> boards);
}
