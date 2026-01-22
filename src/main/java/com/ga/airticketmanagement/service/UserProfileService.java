package com.ga.airticketmanagement.service;

import com.ga.airticketmanagement.dto.mapper.PageMetaFactory;
import com.ga.airticketmanagement.dto.mapper.UserProfileMapper;
import com.ga.airticketmanagement.dto.request.UserProfileRequest;
import com.ga.airticketmanagement.dto.response.ListResponse;
import com.ga.airticketmanagement.dto.response.PageMeta;
import com.ga.airticketmanagement.dto.response.UserProfileResponse;
import com.ga.airticketmanagement.exception.InformationNotFoundException;
import com.ga.airticketmanagement.model.Asset;
import com.ga.airticketmanagement.model.User;
import com.ga.airticketmanagement.model.UserProfile;
import com.ga.airticketmanagement.repository.AssetRepository;
import com.ga.airticketmanagement.repository.UserProfileRepository;
import com.ga.airticketmanagement.security.AuthenticatedUserProvider;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class UserProfileService {

    private final UserProfileMapper userProfileMapper;
    private final UserProfileRepository userProfileRepository;
    private final AuthenticatedUserProvider authenticatedUserProvider;
    private final AssetService assetService;
    private final AssetRepository assetRepository;
    
    public UserProfileService(UserProfileMapper userProfileMapper, UserProfileRepository userProfileRepository,
                              AuthenticatedUserProvider authenticatedUserProvider, AssetService assetService,
                              AssetRepository assetRepository) {
        this.userProfileMapper = userProfileMapper;
        this.userProfileRepository = userProfileRepository;
        this.authenticatedUserProvider = authenticatedUserProvider;
        this.assetService = assetService;
        this.assetRepository = assetRepository;
    }

    public UserProfileResponse createUserProfile(UserProfileRequest userProfileObject){

        User user = authenticatedUserProvider.getAuthenticatedUser();

        UserProfile userProfile = userProfileMapper.toEntity(userProfileObject);
        userProfile.setUser(user);
        userProfile = userProfileRepository.save(userProfile);
        return userProfileMapper.toResponse(userProfile);
    }

    public UserProfileResponse getUserProfile(Long id){

        UserProfile userProfile = userProfileRepository.findById(id).orElseThrow(
                () -> new InformationNotFoundException("UserProfile with id " + id + " not found")
        );

        return  userProfileMapper.toResponse(userProfile);
    }


    public ListResponse<UserProfileResponse> getUserProfiles(Pageable pageable) {

        Page<UserProfile> page = userProfileRepository.findAll(pageable);

        List<UserProfileResponse> data = page.getContent().stream()
                .map(userProfileMapper::toResponse).toList();

        PageMeta meta = PageMetaFactory.from(page);

        return new ListResponse<>(data, meta);
    }

    public UserProfileResponse updateUserProfile(Long id, UserProfileRequest userProfileObject){

        UserProfile userProfile = userProfileRepository.findById(id).orElseThrow(
                () -> new InformationNotFoundException("UserProfile " + id + " does not exist.")
        );

        userProfile.setFirstName(userProfileObject.getFirstName());
        userProfile.setLastName(userProfileObject.getLastName());
        userProfile.setProfileImg(userProfileObject.getProfileImg());
        UserProfile updatedUserProfile = userProfileRepository.save(userProfile);

        return userProfileMapper.toResponse(updatedUserProfile);
    }


    public void deleteUserProfile(Long id){

        UserProfile userProfile = userProfileRepository.findById(id).orElseThrow(
                () -> new InformationNotFoundException("UserProfile " + id + " does not exist")
        );

        userProfileRepository.delete(userProfile);
    }

    @Transactional
    public UserProfileResponse updateMyUserProfile(UserProfileRequest userProfileRequest, MultipartFile profileImage) throws IOException {
        User user = authenticatedUserProvider.getAuthenticatedUser();
        
        // Get or create user profile
        UserProfile userProfile = user.getUserProfile();
        if (userProfile == null) {
            userProfile = new UserProfile();
            userProfile.setUser(user);
            user.setUserProfile(userProfile);
        }
        
        // Update basic profile fields
        if (userProfileRequest.getFirstName() != null && !userProfileRequest.getFirstName().isBlank()) {
            userProfile.setFirstName(userProfileRequest.getFirstName());
        }
        if (userProfileRequest.getLastName() != null && !userProfileRequest.getLastName().isBlank()) {
            userProfile.setLastName(userProfileRequest.getLastName());
        }
        
        // Handle profile image upload
        if (profileImage != null && !profileImage.isEmpty()) {
            // Validate that it's an image
            String contentType = profileImage.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                throw new IllegalArgumentException("Only image files are allowed");
            }
            
            // Delete old profile image if exists
            if (userProfile.getProfileImg() != null && !userProfile.getProfileImg().isBlank()) {
                Asset oldImage = assetRepository.findByFileName(userProfile.getProfileImg()).orElse(null);
                if (oldImage != null && oldImage.getUser().getId().equals(user.getId())) {
                    assetService.deleteImage(oldImage.getId());
                }
            }
            
            // Save new image
            Asset savedImage = assetService.saveImage(profileImage, user.getId());
            userProfile.setProfileImg(savedImage.getFileName());
        } else if (userProfileRequest.getProfileImg() != null) {
            // If profileImg is provided in request but no file, update the reference
            userProfile.setProfileImg(userProfileRequest.getProfileImg());
        }
        
        // Save updated profile
        UserProfile updatedUserProfile = userProfileRepository.save(userProfile);
        
        return userProfileMapper.toResponse(updatedUserProfile);
    }
}