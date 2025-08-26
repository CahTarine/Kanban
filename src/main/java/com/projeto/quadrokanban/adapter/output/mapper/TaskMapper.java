package com.projeto.quadrokanban.adapter.output.mapper;

import java.util.List;

import org.mapstruct.Mapper;

import com.projeto.quadrokanban.adapter.output.entity.TaskEntity;
import com.projeto.quadrokanban.core.domain.model.Task;

@Mapper(componentModel = "spring")
public interface TaskMapper {


    Task toDomain(TaskEntity taskEntity);

    TaskEntity toEntity(Task task);
    
    List<Task> toDomainList(List<TaskEntity> entities);
    List<TaskEntity> toEntityList(List<Task> tasks);
}
