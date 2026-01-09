package com.fredande.rewardsappbackend.service;

import com.fredande.rewardsappbackend.config.CustomUserDetails;
import com.fredande.rewardsappbackend.dto.ChildRegistrationRequest;
import com.fredande.rewardsappbackend.dto.ParentRegistrationRequest;
import org.springframework.security.core.userdetails.UserDetails;

public interface AuthenticationServiceDef {

    UserDetails authenticate(String username, String password);

    String generateToken(UserDetails userDetails);

    void registerParent(ParentRegistrationRequest parentRegistrationRequest);

    void registerChild(ChildRegistrationRequest childRegistrationRequest,
                       CustomUserDetails customUserDetails);

}
