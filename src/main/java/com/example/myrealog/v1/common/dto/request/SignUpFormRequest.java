package com.example.myrealog.v1.common.dto.request;

import lombok.Data;

@Data
public class SignUpFormRequest {

    private String email;
    private String username;
    private String displayName;
    private String bio;
}
