package com.projeto.quadrokanban.adapter.output.mapper;

import org.mapstruct.Mapper;

import com.projeto.quadrokanban.adapter.output.entity.UserEntity;
import com.projeto.quadrokanban.core.domain.model.User;

@Mapper(componentModel = "spring")
public interface UserMapper {
	
	User toDomain(UserEntity userEntity);
	
	UserEntity toEntity(User user);

}
