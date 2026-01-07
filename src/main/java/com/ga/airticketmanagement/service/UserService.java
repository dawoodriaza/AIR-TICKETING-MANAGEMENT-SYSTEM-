package com.ga.airticketmanagement.service;

import com.ga.airticketmanagement.model.User;
import com.ga.airticketmanagement.model.request.LoginRequest;
import com.ga.airticketmanagement.model.response.LoginResponse;
import com.ga.airticketmanagement.repository.UserRepository;
import com.ga.airticketmanagement.security.JWTUtils;
import com.ga.airticketmanagement.security.MyUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.server.ResponseStatusException;

import javax.swing.plaf.synth.SynthOptionPaneUI;

@Service
public class UserService {
    private  UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JWTUtils jwtUtils;

    private final AuthenticationManager authenticationManager;
    private MyUserDetails myUserDetails;
    public UserService(UserRepository userRepository, @Lazy PasswordEncoder passwordEncoder,
                       JWTUtils jwtUtils,
                       @Lazy AuthenticationManager authenticationManager,
                       @Lazy MyUserDetails myUserDetails){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
        this.authenticationManager = authenticationManager;
        this.myUserDetails = myUserDetails;

    }


//    public void setUserRepository(UserRepository userRepository) {
//        this.userRepository = userRepository;
//    }
    public User createUser(User userObject){
        System.out.println("Calling createUser from the Service ==>");
        if (!userRepository.existsByEmailAddress(userObject.getEmailAddress())){
             userObject.setPassword(passwordEncoder.encode(userObject.getPassword()));
            return userRepository.save(userObject);
        }else {
              throw  new ResponseStatusException(HttpStatus.CONFLICT,"User with email address already exists");
        }
    }

    public User findUserByEmailAddress(String email){
        return userRepository.findUserByEmailAddress(email);
    }
    public ResponseEntity<?> loginUser(LoginRequest loginRequest) {
        System.out.println(loginRequest.getPassword());
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword());
        try {
            Authentication authentication = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            myUserDetails = (MyUserDetails) authentication.getPrincipal();
            final String JWT = jwtUtils.generateJwtToken(myUserDetails);
            return ResponseEntity.ok(new LoginResponse(JWT));
        } catch (Exception e) {
            return ResponseEntity.ok(new LoginResponse("Error : user name or password is incorrect"));
        }
    }
}
