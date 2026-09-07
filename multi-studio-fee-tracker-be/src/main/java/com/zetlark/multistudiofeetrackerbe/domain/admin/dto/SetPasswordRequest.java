package com.zetlark.multistudiofeetrackerbe.domain.admin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SetPasswordRequest {

    @NotBlank
    @Size(min = 8, message = "La password deve contenere almeno 8 caratteri.")
    private String password;
}
