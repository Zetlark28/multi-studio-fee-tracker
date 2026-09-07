package com.zetlark.multistudiofeetrackerbe.domain.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CurrentUser {

    private String username;
    private Boolean isAdmin;
}
