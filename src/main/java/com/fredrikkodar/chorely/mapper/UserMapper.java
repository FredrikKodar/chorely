package com.fredrikkodar.chorely.mapper;

import com.fredrikkodar.chorely.dto.UserIdAndFirstNameResponse;
import com.fredrikkodar.chorely.dto.ChildResponse;
import com.fredrikkodar.chorely.dto.UserResponse;
import com.fredrikkodar.chorely.enums.TaskStatus;
import com.fredrikkodar.chorely.model.Task;
import com.fredrikkodar.chorely.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    ChildResponse userToChildResponse(User user);

    UserIdAndFirstNameResponse userToUserIdAndFirstNameResponse(User user);

    @Mapping(target = "numTasksOpen", expression = "java(getNumOpenTasks(user))")
    @Mapping(target = "numTasksCompleted", expression = "java(getNumCompletedTasks(user))")
    @Mapping(target = "numTasksTotal", expression = "java(getNumOpenTasks(user) + getNumCompletedTasks(user))")
    UserResponse userToUserResponse(User user);

    default Integer getNumOpenTasks(User user) {
        Integer numTasks = 0;
        List<Task> tasks = user.getTasks();
        for (Task task : tasks) {
            if (!task.getStatus().equals(TaskStatus.APPROVED)) {
                numTasks++;
            }
        }
        return numTasks;
    }

    default Integer getNumCompletedTasks(User user) {
        Integer numTasks = 0;
        List<Task> tasks = user.getTasks();
        for (Task task : tasks) {
            if (task.getStatus().equals(TaskStatus.APPROVED)) {
                numTasks++;
            }
        }
        return numTasks;
    }

}
