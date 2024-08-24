package com.vivek.garg.book_network.auth;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AuthenticationRequest {
    @NotNull(message = "email can not be null")
    @NotEmpty(message = "email can not be empty.")
    private String email;
    @Size(min = 8, message = "size is small than 8")
    private String password;
}
