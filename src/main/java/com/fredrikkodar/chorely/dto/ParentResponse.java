package com.fredrikkodar.chorely.dto;

import com.fredrikkodar.chorely.model.User;

import java.util.List;

public record ParentResponse(Integer id,
                             String email,
                             String firstName,
                             String lastName,
                             List<User> children) {

}
