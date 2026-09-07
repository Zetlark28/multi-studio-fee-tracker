package com.zetlark.multistudiofeetrackerbe.domain.dashboard.controller;

import com.zetlark.multistudiofeetrackerbe.domain.dashboard.service.DashboardService;
import com.zetlark.multistudiofeetrackerbe.domain.dashboard.dto.DashdboardDataDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DashboardController implements DashboardControllerApi {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @Override
    public ResponseEntity<DashdboardDataDto> getDashboardData(Long month) {
        return ResponseEntity.ok(dashboardService.getData(month));
    }
}
