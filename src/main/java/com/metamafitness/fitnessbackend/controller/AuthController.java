package com.metamafitness.fitnessbackend.controller;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.metamafitness.fitnessbackend.common.CoreConstant;
import com.metamafitness.fitnessbackend.dto.*;
import com.metamafitness.fitnessbackend.exception.BusinessException;
import com.metamafitness.fitnessbackend.exception.ElementAlreadyExistException;
import com.metamafitness.fitnessbackend.exception.ElementNotFoundException;
import com.metamafitness.fitnessbackend.exception.UnauthorizedException;
import com.metamafitness.fitnessbackend.model.GenericEnum;
import com.metamafitness.fitnessbackend.model.User;
import com.metamafitness.fitnessbackend.service.MailSenderService;
import com.metamafitness.fitnessbackend.service.UserService;
import com.metamafitness.fitnessbackend.utils.JwtProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController extends GenericController<User, UserDto> {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;

    private final
    JwtProvider jwtProvider;

    private final MailSenderService mailSenderService;


    public AuthController(UserService userService, AuthenticationManager authenticationManager, JwtProvider jwtProvider,
                          MailSenderService mailSenderService) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtProvider = jwtProvider;

        this.mailSenderService = mailSenderService;
    }


    private String getSiteURL(HttpServletRequest request) {
        String siteURL = request.getRequestURL().toString();
        return siteURL.replace(request.getServletPath(), "");
    }


    @PostMapping("/register")
    public ResponseEntity<UserDto> saveUser(@RequestBody UserDto userDto, HttpServletRequest request)
            throws ElementAlreadyExistException {
        User convertedUser = convertToEntity(userDto);
        userService.generateVerificationCode(convertedUser);
        User savedUser = userService.save(convertedUser);

        userService.sendVerificationEmail(savedUser, getSiteURL(request));

        return new ResponseEntity<>(convertToDto(savedUser), HttpStatus.CREATED);
    }

    @GetMapping("/verify")
    public ResponseEntity<String> verifyUser(@RequestParam("code") String code) {
        if (userService.verify(code)) {
            return new ResponseEntity<>("verify_success", HttpStatus.OK);
        }
        return new ResponseEntity<>("verify_fail", HttpStatus.UNAUTHORIZED);
    }

    @PostMapping("/login")
    public ResponseEntity<JwtTokenResponseDto> login(@RequestBody UserLoginDto userLoginDto)
            throws UnauthorizedException, ElementNotFoundException {

        Authentication authToken =
                new UsernamePasswordAuthenticationToken(userLoginDto.getEmail(), userLoginDto.getPassword());

        Authentication authResult;

        try {
            authResult = authenticationManager.authenticate(authToken);
        } catch (org.springframework.security.core.AuthenticationException e) {
            throw new UnauthorizedException(null, e.getCause(), CoreConstant.Exception.AUTHENTICATION_BAD_CREDENTIALS, null);
        }

        User authenticatedUser = (User) authResult.getPrincipal();

        JwtToken accessToken = jwtProvider.generateToken(authenticatedUser, GenericEnum.JwtTokenType.ACCESS);
        JwtToken refreshToken = jwtProvider.generateToken(authenticatedUser, GenericEnum.JwtTokenType.REFRESH);
        String refreshTokenId = jwtProvider.getDecodedJWT(refreshToken.getToken(), GenericEnum.JwtTokenType.REFRESH).getId();

        User connectedUser = userService.findById(authenticatedUser.getId());
        connectedUser.setRefreshTokenId(refreshTokenId);
        userService.update(connectedUser.getId(), connectedUser);
        return ResponseEntity
                .ok()
                .body(
                        JwtTokenResponseDto.builder()
                                .accessToken(accessToken)
                                .refreshToken(refreshToken)
                                .build()
                );
    }

    @PostMapping("/forget-password")
    public ResponseEntity<ForgetPasswordResponse> sendForgetPassword(@RequestBody ForgetPasswordRequest forgetPasswordRequest, HttpServletRequest request) {
        User user = userService.findByEmail(forgetPasswordRequest.getEmail());
        JwtToken resetToken = userService.generateResetPasswordToken(user);
        Map<String, Object> mailModel = new HashMap<>();
        mailModel.put("token", resetToken.getToken());
        mailModel.put("user", user);
        mailModel.put("signature", "https://fitness-app.com");
        mailModel.put("resetUrl", getSiteURL(request) + "api/auth/resetPassword?code=" + resetToken.getToken());
        mailSenderService.sendEmail(user.getEmail(), "reset password", mailModel, "reset-password.html");

        return ResponseEntity.ok().body(ForgetPasswordResponse.builder().message("email send successfully").build());

    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest resetPasswordRequest,
                                           HttpServletRequest request){
        String jwtResetToken = jwtProvider.extractTokenFromRequest(request);
        DecodedJWT decodedResetToken = jwtProvider.getDecodedJWT(jwtResetToken, GenericEnum.JwtTokenType.RESET);
        Long userId = Long.valueOf(decodedResetToken.getSubject());
        String resetTokenId = decodedResetToken .getId();

        User user = userService.findById(userId);

        try {
            if (!resetTokenId.equals(user.getResetId()))
                throw new UnauthorizedException(null, new UnauthorizedException(), CoreConstant.Exception.AUTHORIZATION_INVALID_TOKEN, null);
        } catch (NullPointerException e) {
            throw new BusinessException(e.getMessage(), e.getCause(), null, null);
        }
        userService.resetPassword(user, resetPasswordRequest.getNewPassword());

        return ResponseEntity.ok().build();
    }

    @PostMapping("/token")
    @Operation(security = {@SecurityRequirement(name = "bearer-key")})
    public ResponseEntity<JwtTokenResponseDto> refreshToken(HttpServletRequest request) throws BusinessException {

        String refreshToken = jwtProvider.extractTokenFromRequest(request);

        DecodedJWT decodedRefreshToken = jwtProvider.getDecodedJWT(refreshToken, GenericEnum.JwtTokenType.REFRESH);
        Long userId = Long.valueOf(decodedRefreshToken.getSubject());
        String refreshTokenId = decodedRefreshToken.getId();

        User user = userService.findById(userId);

        try {
            if (!refreshTokenId.equals(user.getRefreshTokenId()))
                throw new UnauthorizedException(null, new UnauthorizedException(), CoreConstant.Exception.AUTHORIZATION_INVALID_TOKEN, null);
        } catch (NullPointerException e) {
            throw new BusinessException(e.getMessage(), e.getCause(), null, null);
        }

        JwtToken newAccessToken = jwtProvider.generateToken(user, GenericEnum.JwtTokenType.ACCESS);
        JwtToken newRefreshToken = jwtProvider.generateToken(user, GenericEnum.JwtTokenType.REFRESH);

        user.setRefreshTokenId(jwtProvider.getDecodedJWT(newRefreshToken.getToken(), GenericEnum.JwtTokenType.REFRESH).getId());
        userService.update(userId, user);

        return ResponseEntity
                .ok()
                .body(
                        JwtTokenResponseDto.builder()
                                .accessToken(newAccessToken)
                                .refreshToken(newRefreshToken)
                                .build()
                );
    }
}
