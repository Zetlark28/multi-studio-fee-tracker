package com.zetlark.multistudiofeetrackerbe.domain.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

    @NotBlank
    @Size(min = 3, message = "L'username deve contenere almeno 3 caratteri.")
    private String username;

    @NotBlank
    @Size(min = 8, message = "La password deve contenere almeno 8 caratteri.")
    private String password;
}
