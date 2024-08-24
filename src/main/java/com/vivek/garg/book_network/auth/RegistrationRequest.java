package com.vivek.garg.book_network.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.*;


@Getter
@Setter
@Builder
public class RegistrationRequest {
    @NotEmpty(message = "firstName can not be empty.")
    @NotBlank(message = "firstName can not be blank.")
    private String firstName;
    @NotEmpty(message = "lastName can not be empty.")
    @NotBlank(message = "lastName can not be blank.")
    private String lastName;
    @NotEmpty(message = "email can not be empty.")
    @NotBlank(message = "email can not be blank.")
    @Email(message = "email is not valid.")
    private String email;
    @NotEmpty(message = "password can not be empty.")
    @NotBlank(message = "password can not be blank.")
    @Size(min = 8, message = "Password can not be less than smaller than 8 characters.")
    private String password;
}
