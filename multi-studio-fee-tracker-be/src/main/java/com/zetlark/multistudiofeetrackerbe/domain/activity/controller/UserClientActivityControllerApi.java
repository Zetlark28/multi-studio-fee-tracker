package com.zetlark.multistudiofeetrackerbe.domain.activity.controller;

import com.zetlark.multistudiofeetrackerbe.application.common.dto.ResponseList;
import com.zetlark.multistudiofeetrackerbe.application.common.exception.ResponseError;
import com.zetlark.multistudiofeetrackerbe.domain.activity.dto.UserClientActivityDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "Registrazione attività", description = "Attività fatturabili registrate per un cliente. price/fee sono sempre calcolati dal server.")
@ApiResponses({
    @ApiResponse(
            responseCode = "401",
            description = "Token mancante o non valido",
            content = @Content(schema = @Schema(implementation = ResponseError.class)))
})
@RequestMapping("/user-client-activities")
public interface UserClientActivityControllerApi {

    @Operation(
            summary = "Elenca le attività di un mese",
            description = "Tutte le attività dell'utente autenticato per il mese indicato (non paginato).")
    @ApiResponse(responseCode = "200", description = "Elenco attività del mese")
    @GetMapping("/history")
    ResponseEntity<List<UserClientActivityDto>> findAllForMonth(
            @Parameter(description = "Mese, come epoch/timestamp (vedi UserClientActivityDto.date)") @RequestParam
                    Long month);

    @Operation(
            summary = "Registra un'attività",
            description = "price e fee sono calcolati dal server in base al type del cliente indicato in clientId "
                    + "(DAILY: valorizzare quantity; PERCENT: valorizzare services). Un valore inviato per price/fee viene ignorato.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Attività registrata"),
        @ApiResponse(
                responseCode = "400",
                description = "Dati non validi o incoerenti con il type del cliente",
                content = @Content(schema = @Schema(implementation = ResponseError.class)))
    })
    @PostMapping
    ResponseEntity<UserClientActivityDto> create(@Valid @RequestBody UserClientActivityDto dto);

    @Operation(summary = "Dettaglio di un'attività")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Attività trovata"),
        @ApiResponse(
                responseCode = "404",
                description = "Attività inesistente, o esistente ma di un altro utente",
                content = @Content(schema = @Schema(implementation = ResponseError.class)))
    })
    @GetMapping("/{id}")
    ResponseEntity<UserClientActivityDto> getById(@Parameter(description = "Id dell'attività") @PathVariable Long id);

    @Operation(summary = "Aggiorna un'attività")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Attività aggiornata"),
        @ApiResponse(
                responseCode = "400",
                description = "Dati non validi o incoerenti con il type del cliente",
                content = @Content(schema = @Schema(implementation = ResponseError.class))),
        @ApiResponse(
                responseCode = "404",
                description = "Attività inesistente, o esistente ma di un altro utente",
                content = @Content(schema = @Schema(implementation = ResponseError.class)))
    })
    @PutMapping("/{id}")
    ResponseEntity<UserClientActivityDto> update(
            @Parameter(description = "Id dell'attività") @PathVariable Long id,
            @Valid @RequestBody UserClientActivityDto dto);

    @Operation(
            summary = "Elenca le attività",
            description = "Elenco paginato, filtrabile per i campi valorizzati in UserClientActivityDto.")
    @ApiResponse(responseCode = "200", description = "Pagina di risultati")
    @GetMapping
    ResponseEntity<ResponseList<UserClientActivityDto>> findAll(
            @ModelAttribute UserClientActivityDto filter, Pageable pageable);

    @Operation(summary = "Elimina un'attività")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Attività eliminata"),
        @ApiResponse(
                responseCode = "404",
                description = "Attività inesistente, o esistente ma di un altro utente",
                content = @Content(schema = @Schema(implementation = ResponseError.class)))
    })
    @DeleteMapping("/{id}")
    ResponseEntity<Void> delete(@Parameter(description = "Id dell'attività") @PathVariable Long id);
}
