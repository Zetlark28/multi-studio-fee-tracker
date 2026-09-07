package com.zetlark.multistudiofeetrackerbe.domain.admin.controller;

import com.zetlark.multistudiofeetrackerbe.application.common.exception.ResponseError;
import com.zetlark.multistudiofeetrackerbe.domain.admin.dto.CreateUserRequest;
import com.zetlark.multistudiofeetrackerbe.domain.admin.dto.SetPasswordRequest;
import com.zetlark.multistudiofeetrackerbe.domain.appuser.dto.AppUserSummaryDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "Admin - Utenti", description = "Gestione utenti, riservata all'admin seminato al boot.")
@ApiResponses({
    @ApiResponse(
            responseCode = "403",
            description = "Chi chiama non è un admin",
            content = @Content(schema = @Schema(implementation = ResponseError.class)))
})
@RequestMapping("/admin/users")
public interface AdminUserControllerApi {

    @Operation(summary = "Elenca tutti gli utenti", description = "Username e ruolo per ogni AppUser, mai la password.")
    @ApiResponse(responseCode = "200", description = "Elenco utenti")
    @GetMapping
    ResponseEntity<List<AppUserSummaryDto>> list();

    @Operation(summary = "Crea un nuovo utente normale")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Utente creato"),
        @ApiResponse(
                responseCode = "400",
                description = "Dati non validi o username già in uso",
                content = @Content(schema = @Schema(implementation = ResponseError.class)))
    })
    @PostMapping
    ResponseEntity<AppUserSummaryDto> create(@Valid @RequestBody CreateUserRequest request);

    @Operation(
            summary = "Imposta una nuova password per un utente",
            description = "Non richiede la password attuale.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Password aggiornata"),
        @ApiResponse(
                responseCode = "404",
                description = "Utente non trovato",
                content = @Content(schema = @Schema(implementation = ResponseError.class)))
    })
    @PutMapping("/{username}/password")
    ResponseEntity<Void> setPassword(
            @Parameter(description = "Username dell'utente target") @PathVariable String username,
            @Valid @RequestBody SetPasswordRequest request);
}
