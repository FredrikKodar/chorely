package com.fredrikkodar.chorely.dto;

public record UserResponse(Integer id,
                           String email,
                           String firstName,
                           String lastName,
                           Integer currentPoints,
                           Integer totalPoints,
                           Integer numTasksOpen,
                           Integer numTasksCompleted,
                           Integer numTasksTotal) {


}
