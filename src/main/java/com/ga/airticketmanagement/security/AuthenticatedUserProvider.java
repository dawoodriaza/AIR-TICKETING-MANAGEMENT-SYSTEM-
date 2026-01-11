package com.ga.airticketmanagement.security;

import com.ga.airticketmanagement.exception.InformationNotFoundException;
import com.ga.airticketmanagement.model.User;
import com.ga.airticketmanagement.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class AuthenticatedUserProvider {

    private final UserRepository userRepository;

    public AuthenticatedUserProvider(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getAuthenticatedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        String email = auth.getName();

        return userRepository.findUserByEmailAddress(email)
                .orElseThrow(() ->
                        new InformationNotFoundException("User does not exist"));

    }
}
