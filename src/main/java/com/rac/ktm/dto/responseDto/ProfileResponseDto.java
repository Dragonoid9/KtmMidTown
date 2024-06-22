package com.rac.ktm.dto.requestDto;

import lombok.Data;

@Data
public class ProfileResponseDto {
    private String userName;
    private String fullName;
    private String email;
    private String phoneNumber;
    private String roles;
}