package com.example.myrealog.dto.request;

import lombok.Data;

@Data
public class SignUpFormRequest {

    private String email;
    private String username;
    private String displayName;
    private String bio;
}
