package com.ga.airticketmanagement.service;

import com.ga.airticketmanagement.dto.mapper.PageMetaFactory;
import com.ga.airticketmanagement.dto.mapper.UserProfileMapper;
import com.ga.airticketmanagement.dto.request.UserProfileRequest;
import com.ga.airticketmanagement.dto.response.ListResponse;
import com.ga.airticketmanagement.dto.response.PageMeta;
import com.ga.airticketmanagement.dto.response.UserProfileResponse;
import com.ga.airticketmanagement.exception.InformationNotFoundException;
import com.ga.airticketmanagement.model.User;
import com.ga.airticketmanagement.model.UserProfile;
import com.ga.airticketmanagement.repository.UserProfileRepository;
import com.ga.airticketmanagement.security.AuthenticatedUserProvider;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserProfileService {

    private final UserProfileMapper userProfileMapper;
    private final UserProfileRepository userProfileRepository;
    private final AuthenticatedUserProvider authenticatedUserProvider;
    public UserProfileService(UserProfileMapper userProfileMapper, UserProfileRepository userProfileRepository,
                              AuthenticatedUserProvider authenticatedUserProvider) {
        this.userProfileMapper = userProfileMapper;
        this.userProfileRepository = userProfileRepository;
        this.authenticatedUserProvider = authenticatedUserProvider;
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
}