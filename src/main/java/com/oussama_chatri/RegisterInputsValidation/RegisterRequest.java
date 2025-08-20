package com.oussama_chatri.RegisterInputsValidation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

    private String firstName;
    private String lastName;
    private Gender gender;
    private LocalDateTime birthDate;
    private String phoneNumber;
    private String email;
    private String username;
    private String password;
    private LocalDateTime createdAt;

}

