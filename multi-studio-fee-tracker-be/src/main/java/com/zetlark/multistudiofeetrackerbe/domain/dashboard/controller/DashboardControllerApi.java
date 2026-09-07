package com.zetlark.multistudiofeetrackerbe.domain.dashboard.controller;

import com.zetlark.multistudiofeetrackerbe.application.common.exception.ResponseError;
import com.zetlark.multistudiofeetrackerbe.domain.dashboard.dto.DashdboardDataDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "Dashboard", description = "Riepilogo mensile dei dati dell'utente autenticato.")
@RequestMapping("/dashboard")
public interface DashboardControllerApi {

    @Operation(
            summary = "Dati di riepilogo per un mese",
            description = "Ricavi totali, giornate lavorate e numero clienti dell'utente autenticato per il mese indicato.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Dati del mese"),
        @ApiResponse(
                responseCode = "401",
                description = "Token mancante o non valido",
                content = @Content(schema = @Schema(implementation = ResponseError.class)))
    })
    @GetMapping
    ResponseEntity<DashdboardDataDto> getDashboardData(
            @Parameter(description = "Mese, come epoch/timestamp") @RequestParam Long month);
}
