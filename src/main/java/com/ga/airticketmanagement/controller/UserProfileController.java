package com.ga.airticketmanagement.controller;

import com.ga.airticketmanagement.dto.request.UserProfileRequest;
import com.ga.airticketmanagement.dto.response.ListResponse;
import com.ga.airticketmanagement.dto.response.UserProfileResponse;
import com.ga.airticketmanagement.service.UserProfileService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

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

    /**
     * Update the authenticated user's own profile (JSON only, no file upload)
     * Allows updating firstName, lastName, and profileImg reference
     * 
     * @param userProfileRequest UserProfileRequest with fields to update
     * @return Updated UserProfileResponse
     */
    @PutMapping(value = "/users/me/profile", consumes = "application/json")
    public ResponseEntity<?> updateMyUserProfileJson(@Valid @RequestBody UserProfileRequest userProfileRequest) {
        try {
            UserProfileResponse response = userProfileService.updateMyUserProfile(userProfileRequest, null);
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to update profile: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }

    /**
     * Update the authenticated user's own profile with file upload
     * Allows updating firstName, lastName, and profile image
     * If a profile image file is provided, it will be saved as ImageEntity and the profileImg field will be updated
     * Accepts multipart/form-data with optional file upload
     * 
     * @param firstName First name (optional)
     * @param lastName Last name (optional)
     * @param profileImg Profile image file name reference (optional, used when no file is uploaded)
     * @param profileImage Profile image file (optional)
     * @return Updated UserProfileResponse
     */
    @PutMapping(value = "/users/me/profile/upload", consumes = "multipart/form-data")
    public ResponseEntity<?> updateMyUserProfileWithFile(
            @RequestParam(value = "firstName", required = false) String firstName,
            @RequestParam(value = "lastName", required = false) String lastName,
            @RequestParam(value = "profileImg", required = false) String profileImg,
            @RequestParam(value = "file", required = false) MultipartFile profileImage) {
        
        try {
            UserProfileRequest userProfileRequest = new UserProfileRequest();
            if (firstName != null && !firstName.isBlank()) {
                userProfileRequest.setFirstName(firstName);
            }
            if (lastName != null && !lastName.isBlank()) {
                userProfileRequest.setLastName(lastName);
            }
            if (profileImg != null && !profileImg.isBlank()) {
                userProfileRequest.setProfileImg(profileImg);
            }
            
            UserProfileResponse response = userProfileService.updateMyUserProfile(userProfileRequest, profileImage);
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to update profile: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }
}