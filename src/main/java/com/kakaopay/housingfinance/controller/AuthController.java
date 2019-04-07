package com.kakaopay.housingfinance.controller;

import com.kakaopay.housingfinance.dto.auth.SignInRequest;
import com.kakaopay.housingfinance.dto.auth.SignUpRequest;
import com.kakaopay.housingfinance.dto.auth.TokenResponse;
import com.kakaopay.housingfinance.security.CustomUserDetails;
import com.kakaopay.housingfinance.security.CustomUserDetailsService;
import com.kakaopay.housingfinance.security.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RequestMapping("/api/v1/auth")
@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;
    private final CustomUserDetailsService userDetailsService;

    @PostMapping("/signin")
    public TokenResponse signin(@RequestBody @Valid SignInRequest signInRequest) {
        authenticate(signInRequest);

        CustomUserDetails userDetails = (CustomUserDetails) userDetailsService.loadUserByUsername(signInRequest.getUsername());
        return new TokenResponse(jwtProvider.generateToken(userDetails));
    }

    private Authentication authenticate(SignInRequest signInRequest) {
        return authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(signInRequest.getUsername(), signInRequest.getPassword())
        );
    }

    @PostMapping( "/signup" )
    public TokenResponse signup(@RequestBody @Valid SignUpRequest signUpRequest) {
        CustomUserDetails userDetails = userDetailsService.signUp(signUpRequest);

        return new TokenResponse(jwtProvider.generateToken(userDetails));
    }

    @PostMapping(value = "/refresh")
    public TokenResponse refresh() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        return new TokenResponse(jwtProvider.generateToken((CustomUserDetails)authentication.getPrincipal()));
    }

}
