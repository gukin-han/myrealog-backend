package com.example.myrealog.auth;

import lombok.Data;
import lombok.Getter;

@Data
@Getter
public class SignupResponse {

    String email;
    String provider;

    public SignupResponse(String email, String provider) {
        this.email = email;
        this.provider = provider;
    }
}
