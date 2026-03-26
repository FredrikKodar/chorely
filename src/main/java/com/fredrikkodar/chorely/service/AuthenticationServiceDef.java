package com.fredrikkodar.chorely.service;

import com.fredrikkodar.chorely.config.CustomUserDetails;
import com.fredrikkodar.chorely.dto.ChildRegistrationRequest;
import com.fredrikkodar.chorely.dto.ParentRegistrationRequest;
import org.springframework.security.core.userdetails.UserDetails;

public interface AuthenticationServiceDef {

    UserDetails authenticate(String username, String password);

    String generateToken(UserDetails userDetails);

    void registerParent(ParentRegistrationRequest parentRegistrationRequest);

    void registerChild(ChildRegistrationRequest childRegistrationRequest,
                       CustomUserDetails customUserDetails);

}
