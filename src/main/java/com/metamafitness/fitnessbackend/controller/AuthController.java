package com.metamafitness.fitnessbackend.controller;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.metamafitness.fitnessbackend.common.CoreConstant;
import com.metamafitness.fitnessbackend.dto.JwtToken;
import com.metamafitness.fitnessbackend.dto.JwtTokenResponseDto;
import com.metamafitness.fitnessbackend.dto.UserDto;
import com.metamafitness.fitnessbackend.dto.UserLoginDto;
import com.metamafitness.fitnessbackend.exception.BusinessException;
import com.metamafitness.fitnessbackend.exception.ElementAlreadyExistException;
import com.metamafitness.fitnessbackend.exception.ElementNotFoundException;
import com.metamafitness.fitnessbackend.exception.UnauthorizedException;
import com.metamafitness.fitnessbackend.model.GenericEnum;
import com.metamafitness.fitnessbackend.model.User;
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

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;

@RestController
@RequestMapping("/api/auth")
public class AuthController extends GenericController<User, UserDto> {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;

    private final
    JwtProvider jwtProvider;



    public AuthController(UserService userService, AuthenticationManager authenticationManager, JwtProvider jwtProvider) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtProvider = jwtProvider;
    }


    private String getSiteURL(HttpServletRequest request) {
        String siteURL = request.getRequestURL().toString();
        return siteURL.replace(request.getServletPath(), "");
    }


    @PostMapping("/register")
    public ResponseEntity<UserDto> saveUser(@RequestBody UserDto userDto, HttpServletRequest request)
            throws ElementAlreadyExistException, UnsupportedEncodingException, MessagingException {
        User convertedUser = convertToEntity(userDto);
        userService.generateVerificationCode(convertedUser);
        User savedUser = userService.save(convertedUser);

        userService.sendVerificationEmail(savedUser, getSiteURL(request));

        return ResponseEntity.ok().body(convertToDto(savedUser));
    }

    @GetMapping("/verify")
    public ResponseEntity<String> verifyUser(@RequestParam("code") String code) {
        if( userService.verify(code)) {
            return new ResponseEntity<String>("verify_success", HttpStatus.OK);
        }
        return new ResponseEntity<String>("verify_fail", HttpStatus.UNAUTHORIZED);
    }

    @PostMapping("/login")
    public ResponseEntity<JwtTokenResponseDto> login( @RequestBody UserLoginDto userLoginDto)
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

    @PostMapping("/token")
    @Operation(security = { @SecurityRequirement(name = "bearer-key") })
    public ResponseEntity<JwtTokenResponseDto> refreshToken(HttpServletRequest request, HttpServletResponse response) throws BusinessException {

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
