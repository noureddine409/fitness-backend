package com.metamafitness.fitnessbackend.controller;

import com.metamafitness.fitnessbackend.common.CoreConstant;
import com.metamafitness.fitnessbackend.dto.AuthenticatedResetPasswordRequest;
import com.metamafitness.fitnessbackend.dto.UserDto;
import com.metamafitness.fitnessbackend.dto.UserPatchDto;
import com.metamafitness.fitnessbackend.exception.UnauthorizedException;
import com.metamafitness.fitnessbackend.model.User;
import com.metamafitness.fitnessbackend.service.StorageService;
import com.metamafitness.fitnessbackend.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.net.URI;
import java.util.Objects;

@RestController
@RequestMapping("/api/users")
public class UserController extends GenericController<User, UserDto> {

    private final UserService userService;

    private final StorageService storageService;

    public UserController(UserService userService, StorageService storageService) {
        this.userService = userService;
        this.storageService = storageService;
    }

    @Override
    public ModelMapper getModelMapper() {
        return super.getModelMapper();
    }


    @GetMapping("/me")
    public ResponseEntity<UserDto> getMe() {
        return new ResponseEntity<>(convertToDto(getCurrentUser()), HttpStatus.OK);
    }

    @PatchMapping
    public ResponseEntity<UserDto> update(@RequestBody @Valid UserPatchDto userDto) {
        User currentUser = getCurrentUser();
        ModelMapper modelMapper = getModelMapper();

        // Save the original skipNullEnabled value
        boolean originalSkipNullEnabled = modelMapper.getConfiguration().isSkipNullEnabled();

        // Set skipNullEnabled to true for this mapping operation
        modelMapper.getConfiguration().setSkipNullEnabled(true);
        modelMapper.map(userDto, currentUser);

        // Set skipNullEnabled back to its original value
        modelMapper.getConfiguration().setSkipNullEnabled(originalSkipNullEnabled);
        currentUser.setProfileCompleted(Boolean.TRUE);
        User updated = userService.update(currentUser.getId(), currentUser);

        return new ResponseEntity<>(convertToDto(updated), HttpStatus.OK);
    }

    @PatchMapping("/profile-picture")
    public ResponseEntity<UserDto> changeProfilePicture(@RequestParam("profile-picture") MultipartFile profilePicture) {
        User currentUser = getCurrentUser();
        final String currentProfilePictureURL = currentUser.getProfilePicture();
        if(!( Objects.isNull(currentProfilePictureURL) || currentProfilePictureURL.isEmpty() || currentProfilePictureURL.isBlank())){
            URI uri = URI.create(currentProfilePictureURL);
            String currentProfilePicture = uri.getPath().substring(1);
            storageService.delete(currentProfilePicture);
        }
        String profilePicturePath = storageService.save(profilePicture);
        currentUser.setProfilePicture(profilePicturePath);
        User updated = userService.update(currentUser.getId(), currentUser);
        return new ResponseEntity<>(convertToDto(updated), HttpStatus.OK);
    }

    @PatchMapping("/reset-password")
    public ResponseEntity<UserDto> resetPassword(@RequestBody @Valid AuthenticatedResetPasswordRequest resetPasswordRequest) {
        User currentUser = getCurrentUser();

        if (userService.checkPassword(currentUser, resetPasswordRequest.getOldPassword())) {
            userService.resetPassword(currentUser, resetPasswordRequest.getNewPassword());
            User updated = userService.update(currentUser.getId(), currentUser);
            return new ResponseEntity<>(convertToDto(updated), HttpStatus.OK);
        }

        throw new UnauthorizedException(new UnauthorizedException(), CoreConstant.Exception.INVALID_PASSWORD, null);

    }

}
