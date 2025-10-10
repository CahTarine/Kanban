package com.projeto.quadrokanban.adapter.output.mapper;

import org.mapstruct.Mapper;

import com.projeto.quadrokanban.adapter.output.entity.BoardEntity;
import com.projeto.quadrokanban.core.domain.model.Board;

@Mapper(componentModel = "spring")
public interface BoardMapper {

    Board toDomain(BoardEntity boardEntity);

    BoardEntity toEntity(Board board);
}
