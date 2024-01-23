package com.example.myrealog.dto.request;

import lombok.Data;

@Data
public class SignupFormRequest {

    private String email;
    private String username;
    private String displayName;
    private String bio;
}
