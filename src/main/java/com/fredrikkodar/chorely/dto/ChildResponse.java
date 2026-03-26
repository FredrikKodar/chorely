package com.fredrikkodar.chorely.dto;

import com.fredrikkodar.chorely.model.User;

import java.util.List;

public record ChildResponse(Integer id,
                            String username,
                            String firstName,
                            String lastName,
                            Integer currentPoints,
                            Integer totalPoints,
                            Integer numTasksOpen,
                            Integer numTasksCompleted,
                            Integer numTasksTotal,
                            List<User> tasks,
                            List<User> parents) {

}
