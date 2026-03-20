package com.fredande.rewardsappbackend.mapper;

import com.fredande.rewardsappbackend.dto.TaskReadResponse;
import com.fredande.rewardsappbackend.dto.TaskSavedResponse;
import com.fredande.rewardsappbackend.model.Task;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface TaskMapper {

    TaskMapper INSTANCE = Mappers.getMapper(TaskMapper.class);

    TaskSavedResponse taskToTaskSavedResponse(Task task);

    default TaskReadResponse taskToTaskReadResponse(Task task) {
        if (task == null) {
            return null;
        }
        
        Integer assignedTo = task.getUser() != null ? task.getUser().getId() : null;
        
        return new TaskReadResponse(
            task.getId(),
            task.getTitle(),
            task.getDescription(),
            task.getPoints(),
            task.getCreated(),
            task.getUpdated(),
            task.getStatus(),
            assignedTo
        );
    }

}
