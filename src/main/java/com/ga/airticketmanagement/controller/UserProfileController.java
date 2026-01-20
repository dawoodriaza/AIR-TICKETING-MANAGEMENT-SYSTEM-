package com.ga.airticketmanagement.controller;

import com.ga.airticketmanagement.dto.request.UserProfileRequest;
import com.ga.airticketmanagement.dto.response.ListResponse;
import com.ga.airticketmanagement.dto.response.UserProfileResponse;
import com.ga.airticketmanagement.service.UserProfileService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api")
public class UserProfileController {

    private UserProfileService userProfileService;

    @Autowired
    public void setUserProfileService(UserProfileService userProfileService){
        this.userProfileService = userProfileService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/user-profiles")
    public UserProfileResponse createUserProfile(@Valid @RequestBody UserProfileRequest userProfile){

        return userProfileService.createUserProfile(userProfile);
    }

    @GetMapping("/user-profiles/{id}")
    public UserProfileResponse getUserProfile(@PathVariable Long id){

        return userProfileService.getUserProfile(id);
    }

    @GetMapping("/user-profiles")
    public ListResponse<UserProfileResponse> getUserProfiles(Pageable pageable){

        return userProfileService.getUserProfiles(pageable);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/user-profiles/{id}")
    public UserProfileResponse updateUserProfile(@PathVariable Long id,@Valid @RequestBody UserProfileRequest userProfile){

        return userProfileService.updateUserProfile(id, userProfile);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/user-profiles/{id}")
    public void deleteUserProfile(@PathVariable Long id){
        userProfileService.deleteUserProfile(id);
    }
}