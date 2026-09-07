package com.zetlark.multistudiofeetrackerbe.domain.auth.controller;

import com.zetlark.multistudiofeetrackerbe.application.common.exception.ResponseError;
import com.zetlark.multistudiofeetrackerbe.domain.auth.dto.AuthResponse;
import com.zetlark.multistudiofeetrackerbe.domain.auth.dto.ChangePasswordRequest;
import com.zetlark.multistudiofeetrackerbe.domain.auth.dto.LoginRequest;
import com.zetlark.multistudiofeetrackerbe.domain.auth.dto.RegisterRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "Auth", description = "Registrazione, login e cambio password. Endpoint pubblici (nessun token richiesto).")
@SecurityRequirements
@RequestMapping("/auth")
public interface AuthControllerApi {

    @Operation(
            summary = "Registra un nuovo utente",
            description = "Crea un nuovo AppUser con ruolo normale ed esegue subito il login, restituendo un token JWT.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Utente creato, token restituito"),
        @ApiResponse(
                responseCode = "400",
                description = "Dati non validi o username già in uso",
                content = @Content(schema = @Schema(implementation = ResponseError.class)))
    })
    @PostMapping("/register")
    ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request);

    @Operation(
            summary = "Autentica un utente esistente",
            description = "Restituisce un token JWT valido per le richieste successive.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Autenticazione riuscita, token restituito"),
        @ApiResponse(
                responseCode = "401",
                description = "Credenziali non valide",
                content = @Content(schema = @Schema(implementation = ResponseError.class)))
    })
    @PostMapping("/login")
    ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request);

    @Operation(
            summary = "Cambia la password di un utente",
            description = "Richiede la password attuale per confermare l'operazione.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Password aggiornata"),
        @ApiResponse(
                responseCode = "400",
                description = "Password attuale errata o nuova password non valida",
                content = @Content(schema = @Schema(implementation = ResponseError.class)))
    })
    @PostMapping("/change-password")
    ResponseEntity<Void> changePassword(@Valid @RequestBody ChangePasswordRequest request);
}
