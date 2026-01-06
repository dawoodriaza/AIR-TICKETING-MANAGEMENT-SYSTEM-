package com.ga.airticketmanagement.controller;

import com.ga.airticketmanagement.model.User;
import com.ga.airticketmanagement.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/auth/users")
public class UserController {
    private UserService userService;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @PostMapping(path = "/register")
    public User createUser(@RequestBody User userObject){
        System.out.println("Calling the createUser from the controller ==>");
        return userService.createUser(userObject);
    }

//    public ResponseEntity<?> loginUser(@RequestBody LoginRequest loginRequest){
//        System.out.println("Calling LoginUser from the controller ==>");
//        return userService.loginUser(loginRequest);
//
//    }

}
