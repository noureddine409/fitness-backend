package com.metamafitness.fitnessbackend.controller;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.facebook.api.impl.FacebookTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController extends GenericController<User, UserDto> {

    @Value("${google.clientId}")
    String googleClientId;

    @Value("${origin.url}")
    private String originApi;

    private final UserService userService;
    private final AuthenticationManager authenticationManager;

    private final JwtProvider jwtProvider;

    private final MailSenderService mailSenderService;


    public AuthController(UserService userService, AuthenticationManager authenticationManager, JwtProvider jwtProvider, MailSenderService mailSenderService) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtProvider = jwtProvider;

        this.mailSenderService = mailSenderService;
    }




    @PostMapping("/google-social-login")
    public ResponseEntity<JwtTokenResponseDto> googleSocialLogin(@RequestBody SocialTokenDto tokenDto) throws UnauthorizedException, IOException {
        GoogleIdTokenVerifier.Builder verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory()).setAudience(Collections.singletonList(googleClientId));
        final GoogleIdToken googleIdToken = GoogleIdToken.parse(verifier.getJsonFactory(), tokenDto.getValue());
        final GoogleIdToken.Payload payload = googleIdToken.getPayload();


        User existingUser = userService.findByEmail_v2(payload.getEmail());

        if (existingUser == null) {
            // User is logging in for the first time with GitHub
            // Create a new user account and generate access and refresh tokens
            User newUser = new User();
            newUser.setEmail(payload.getEmail());
            newUser.setPassword(UUID.randomUUID().toString());
            newUser.setEnabled(Boolean.TRUE);
            newUser.setProfilePicture((String) payload.get("picture"));
            newUser.setLastName((String) payload.get("family_name"));
            newUser.setFirstName((String) payload.get("given_name"));

            // Save the new user account to the database
            newUser = userService.save(newUser);

            // Generate access and refresh tokens
            JwtToken accessToken = jwtProvider.generateToken(newUser, GenericEnum.JwtTokenType.ACCESS);
            JwtToken refreshToken = jwtProvider.generateToken(newUser, GenericEnum.JwtTokenType.REFRESH);

            return ResponseEntity.ok().body(JwtTokenResponseDto.builder().accessToken(accessToken).refreshToken(refreshToken).build());
        } else {
            // User already exists in the database
            // Generate access and refresh tokens and update the refresh token ID
            JwtToken accessToken = jwtProvider.generateToken(existingUser, GenericEnum.JwtTokenType.ACCESS);
            JwtToken refreshToken = jwtProvider.generateToken(existingUser, GenericEnum.JwtTokenType.REFRESH);
            String refreshTokenId = jwtProvider.getDecodedJWT(refreshToken.getToken(), GenericEnum.JwtTokenType.REFRESH).getId();
            existingUser.setEnabled(Boolean.TRUE);
            existingUser.setRefreshTokenId(refreshTokenId);
            userService.update(existingUser.getId(), existingUser);
            return ResponseEntity.ok().body(JwtTokenResponseDto.builder().accessToken(accessToken).refreshToken(refreshToken).build());
        }
    }


    @PostMapping("/facebook-social-login")
    public ResponseEntity<JwtTokenResponseDto> facebookSocialLogin(@RequestBody SocialTokenDto tokenDto) throws UnauthorizedException {
        Facebook facebook = new FacebookTemplate(tokenDto.getValue());
        final String[] fields = {"email", "picture"};
        org.springframework.social.facebook.api.User fbUser = facebook.fetchObject("me", org.springframework.social.facebook.api.User.class, fields);

        User existingUser = userService.findByEmail_v2(fbUser.getEmail());

        if (existingUser == null) {
            // User is logging in for the first time with GitHub
            // Create a new user account and generate access and refresh tokens
            User newUser = new User();
            newUser.setEmail(fbUser.getEmail());
            newUser.setPassword(UUID.randomUUID().toString());
            newUser.setEnabled(Boolean.TRUE);
            newUser.setLastName(fbUser.getLastName());
            newUser.setFirstName(fbUser.getFirstName());

            // Save the new user account to the database
            newUser = userService.save(newUser);

            // Generate access and refresh tokens
            JwtToken accessToken = jwtProvider.generateToken(newUser, GenericEnum.JwtTokenType.ACCESS);
            JwtToken refreshToken = jwtProvider.generateToken(newUser, GenericEnum.JwtTokenType.REFRESH);

            return ResponseEntity.ok().body(JwtTokenResponseDto.builder().accessToken(accessToken).refreshToken(refreshToken).build());
        } else {
            // User already exists in the database
            // Generate access and refresh tokens and update the refresh token ID
            existingUser.setEnabled(Boolean.TRUE);
            JwtToken accessToken = jwtProvider.generateToken(existingUser, GenericEnum.JwtTokenType.ACCESS);
            JwtToken refreshToken = jwtProvider.generateToken(existingUser, GenericEnum.JwtTokenType.REFRESH);
            String refreshTokenId = jwtProvider.getDecodedJWT(refreshToken.getToken(), GenericEnum.JwtTokenType.REFRESH).getId();

            existingUser.setRefreshTokenId(refreshTokenId);
            userService.update(existingUser.getId(), existingUser);
            return ResponseEntity.ok().body(JwtTokenResponseDto.builder().accessToken(accessToken).refreshToken(refreshToken).build());
        }
    }

    @PostMapping("/register")
    public ResponseEntity<UserDto> saveUser(@RequestBody UserDto userDto) throws ElementAlreadyExistException {
        User convertedUser = convertToEntity(userDto);
        userService.generateVerificationCode(convertedUser);
        boolean mailSentFlag = userService.sendVerificationEmail(convertedUser);
        if (mailSentFlag) {
            User savedUser = userService.save(convertedUser);
            return new ResponseEntity<>(convertToDto(savedUser), HttpStatus.CREATED);
        }
        throw new BusinessException();
    }

    @GetMapping("/verify")
    public ResponseEntity<JwtTokenResponseDto> verifyUser(@RequestParam("code") String code) {
        User user = userService.verify(code);
        if (Objects.isNull(user)) {
            throw new UnauthorizedException();
        }
        JwtToken accessToken = jwtProvider.generateToken(user, GenericEnum.JwtTokenType.ACCESS);
        JwtToken refreshToken = jwtProvider.generateToken(user, GenericEnum.JwtTokenType.REFRESH);

        return ResponseEntity.ok().body(JwtTokenResponseDto.builder().accessToken(accessToken).refreshToken(refreshToken).build());
    }

    @PostMapping("/login")
    public ResponseEntity<JwtTokenResponseDto> login(@RequestBody UserLoginDto userLoginDto) throws UnauthorizedException, ElementNotFoundException {

        Authentication authToken = new UsernamePasswordAuthenticationToken(userLoginDto.getEmail(), userLoginDto.getPassword());

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
        return ResponseEntity.ok().body(JwtTokenResponseDto.builder().accessToken(accessToken).refreshToken(refreshToken).build());
    }

    @PostMapping("/forget-password")
    public ResponseEntity<ForgetPasswordResponse> sendForgetPassword(@RequestBody ForgetPasswordRequest forgetPasswordRequest  ) {
        User user = userService.findByEmail(forgetPasswordRequest.getEmail());
        JwtToken resetToken = userService.generateResetPasswordToken(user);
        Map<String, Object> mailModel = new HashMap<>();
        mailModel.put("token", resetToken.getToken());
        mailModel.put("user", user);
        mailModel.put("signature", "https://fitness-app.com");
        mailModel.put("resetUrl", originApi + "/reset-password?code=" + resetToken.getToken());
        mailSenderService.sendEmail(user.getEmail(), "reset password", mailModel, "reset-password.html");

        return ResponseEntity.ok().body(ForgetPasswordResponse.builder().message("email send successfully").build());
    }

    // TODO add end point resend verification mail

    private DecodedJWT getDecodedResetToken(HttpServletRequest request) {
        String jwtResetToken = jwtProvider.extractTokenFromRequest(request);
        return jwtProvider.getDecodedJWT(jwtResetToken, GenericEnum.JwtTokenType.RESET);
    }

    @GetMapping("/forget-password/verify-token")
    public ResponseEntity<JwtToken> sendForgetPassword(HttpServletRequest request) {
        DecodedJWT decodedResetToken = getDecodedResetToken(request);
        Long userId = Long.valueOf(decodedResetToken.getSubject());
        String resetTokenId = decodedResetToken.getId();

        User user = userService.findById(userId);

        try {
            if (!resetTokenId.equals(user.getResetId()))
                throw new UnauthorizedException(null, new UnauthorizedException(), CoreConstant.Exception.AUTHORIZATION_INVALID_TOKEN, null);
        } catch (NullPointerException e) {
            throw new BusinessException(e.getMessage(), e.getCause(), null, null);
        }

        return new ResponseEntity<>(JwtToken.builder().token(decodedResetToken.getToken()).expiresIn(decodedResetToken.getExpiresAtAsInstant()).createdAt(decodedResetToken.getIssuedAtAsInstant()).build(), HttpStatus.OK);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest resetPasswordRequest, HttpServletRequest request) {
        DecodedJWT decodedResetToken = getDecodedResetToken(request);
        Long userId = Long.valueOf(decodedResetToken.getSubject());
        String resetTokenId = decodedResetToken.getId();

        User user = userService.findById(userId);

        try {
            if (!resetTokenId.equals(user.getResetId()))
                throw new UnauthorizedException(null, new UnauthorizedException(), CoreConstant.Exception.AUTHORIZATION_INVALID_TOKEN, null);
        } catch (NullPointerException e) {
            throw new BusinessException(e.getMessage(), e.getCause(), null, null);
        }

        userService.resetPassword(user, resetPasswordRequest.getNewPassword());

        return new ResponseEntity<>(HttpStatus.OK);
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

        return ResponseEntity.ok().body(JwtTokenResponseDto.builder().accessToken(newAccessToken).refreshToken(newRefreshToken).build());
    }
}
