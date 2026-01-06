package com.ga.airticketmanagement.service;

import com.ga.airticketmanagement.model.User;
import com.ga.airticketmanagement.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.server.ResponseStatusException;

@Service
public class UserService {
    private  UserRepository userRepository;


    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    public User createUser( User userObject){
        System.out.println("Calling createUser from the Service ==>");
        if (!userRepository.existsByEmailAddress(userObject.getEmailAddress())){
            return userRepository.save(userObject);
        }else {
              throw  new ResponseStatusException(HttpStatus.CONFLICT,"User with email address already exists");
        }
    }

    public User findUserByEmailAddress(String email){
        return userRepository.findUserByEmailAddress(email);
    }
}
