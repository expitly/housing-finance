package com.kakaopay.housingfinance.dto.auth;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor
public class SignInRequest {
    @NotEmpty
    private String username;
    @NotEmpty
    private String password;

    public SignInRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
