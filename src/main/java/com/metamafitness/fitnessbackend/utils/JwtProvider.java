package com.metamafitness.fitnessbackend.utils;


import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import com.metamafitness.fitnessbackend.common.CoreConstant;
import com.metamafitness.fitnessbackend.dto.JwtToken;
import com.metamafitness.fitnessbackend.exception.BusinessException;
import com.metamafitness.fitnessbackend.exception.UnauthorizedException;
import com.metamafitness.fitnessbackend.model.GenericEnum;
import com.metamafitness.fitnessbackend.model.User;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
@Component
public class JwtProvider {

    @Value("${jwt.access-token.secret}")
    private String accessTokenSecret;

    @Value("${jwt.access-token.expiration-in-mins}")
    private int accessTokenExpirationInMins;

    @Value("${jwt.refresh-token.secret}")
    private String refreshTokenSecret;

    @Value("${jwt.refresh-token.expiration-in-weeks}")
    private int refreshTokenExpirationInWeeks;

    @Value("${jwt.token.issuer}")
    private String tokenIssuer;

    public JwtToken generateToken(User user, GenericEnum.JwtTokenType tokenType) throws BusinessException {
        String secret;
        Instant creationDate = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant();
        Instant expiryDate;
        String token;

        JWTCreator.Builder builder = JWT.create()
                .withJWTId(UUID.randomUUID().toString())
                .withSubject(user.getId().toString())
                .withIssuedAt(creationDate);

        try {
            switch(tokenType) {
                case ACCESS -> {
                    secret = accessTokenSecret;
                    expiryDate = LocalDateTime.now().plusMinutes(accessTokenExpirationInMins)
                            .atZone(ZoneId.systemDefault()).toInstant();

                    List<String> claims = user.getAuthorities().stream()
                            .map(GrantedAuthority::getAuthority)
                            .collect(Collectors.toList());

                    builder
                            .withExpiresAt(expiryDate)
                            .withClaim("roles", claims);
                }
                case REFRESH -> {
                    secret = refreshTokenSecret;
                    expiryDate = LocalDateTime.now().plusWeeks(refreshTokenExpirationInWeeks)
                            .atZone(ZoneId.systemDefault()).toInstant();

                    builder
                            .withExpiresAt(expiryDate);
                }
                default -> throw new BusinessException("Invalid token type", new BusinessException(), null, null);
            }

            token = builder.sign(Algorithm.HMAC256(secret));

        } catch (IllegalArgumentException | JWTCreationException e) {
            throw new BusinessException(e.getMessage(), e.getCause(), null, null);
        }

        return JwtToken.builder()
                .token(token)
                .createdAt(creationDate)
                .expiresIn(expiryDate)
                .build();
    }

    public DecodedJWT getDecodedJWT(String token, GenericEnum.JwtTokenType tokenType) throws UnauthorizedException {
        String secret = (Objects.equals(GenericEnum.JwtTokenType.ACCESS, tokenType)) ? accessTokenSecret : refreshTokenSecret;

        try {
            return JWT
                    .require(Algorithm.HMAC256(secret))
                    .build()
                    .verify(token);
        } catch (JWTVerificationException e) {
            throw new UnauthorizedException(null, e.getCause(), CoreConstant.Exception.AUTHORIZATION_INVALID_TOKEN, null);
        }
    }

    public String extractTokenFromRequest(HttpServletRequest request) throws UnauthorizedException {
        final String bearerPrefix = "Bearer ";

        String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader == null || authorizationHeader.isBlank())
            throw new UnauthorizedException(null, new UnauthorizedException(), CoreConstant.Exception.AUTHORIZATION_MISSING_HEADER, null);

        if (! authorizationHeader.startsWith(bearerPrefix))
            throw new UnauthorizedException(null, new UnauthorizedException(), CoreConstant.Exception.AUTHORIZATION_INVALID_HEADER, null);

        String token = authorizationHeader.replace(bearerPrefix, Strings.EMPTY);

        if (token == null || token.isBlank())
            throw new UnauthorizedException(null, new UnauthorizedException(), CoreConstant.Exception.AUTHORIZATION_MISSING_TOKEN, null);

        return token;
    }

}