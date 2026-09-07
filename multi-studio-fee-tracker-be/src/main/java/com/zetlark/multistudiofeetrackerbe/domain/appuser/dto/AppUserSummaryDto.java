package com.zetlark.multistudiofeetrackerbe.domain.appuser.dto;

import com.zetlark.multistudiofeetrackerbe.domain.appuser.entity.AppUserRole;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppUserSummaryDto {

    private String username;

    private AppUserRole role;
}
