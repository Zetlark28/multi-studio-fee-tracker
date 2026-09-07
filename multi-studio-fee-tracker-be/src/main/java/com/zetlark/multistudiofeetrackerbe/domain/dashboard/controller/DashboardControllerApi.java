package com.zetlark.multistudiofeetrackerbe.domain.dashboard.controller;

import com.zetlark.multistudiofeetrackerbe.domain.dashboard.dto.DashdboardDataDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequestMapping("/dashboard")
public interface DashboardControllerApi {

    @GetMapping
    ResponseEntity<DashdboardDataDto> getDashboardData(@RequestParam Long month);
}
