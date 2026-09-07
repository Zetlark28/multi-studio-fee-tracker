package com.zetlark.multistudiofeetrackerbe.domain.client.controller;

import com.zetlark.multistudiofeetrackerbe.application.common.dto.ResponseList;
import com.zetlark.multistudiofeetrackerbe.application.common.exception.ResponseError;
import com.zetlark.multistudiofeetrackerbe.domain.client.dto.ClientDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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

@Tag(name = "Clienti", description = "Studi gestiti dall'utente autenticato (isolamento per singolo utente, tranne per l'admin).")
@ApiResponses({
    @ApiResponse(
            responseCode = "401",
            description = "Token mancante o non valido",
            content = @Content(schema = @Schema(implementation = ResponseError.class)))
})
@RequestMapping("/clients")
public interface ClientControllerApi {

    @Operation(summary = "Crea un cliente", description = "Il cliente creato viene associato automaticamente all'utente autenticato.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Cliente creato"),
        @ApiResponse(
                responseCode = "400",
                description = "Dati non validi",
                content = @Content(schema = @Schema(implementation = ResponseError.class)))
    })
    @PostMapping
    ResponseEntity<ClientDto> create(@Valid @RequestBody ClientDto dto);

    @Operation(summary = "Dettaglio di un cliente")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Cliente trovato"),
        @ApiResponse(
                responseCode = "404",
                description = "Cliente inesistente, o esistente ma di un altro utente",
                content = @Content(schema = @Schema(implementation = ResponseError.class)))
    })
    @GetMapping("/{id}")
    ResponseEntity<ClientDto> getById(@Parameter(description = "Id del cliente") @PathVariable Long id);

    @Operation(summary = "Aggiorna un cliente")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Cliente aggiornato"),
        @ApiResponse(
                responseCode = "400",
                description = "Dati non validi",
                content = @Content(schema = @Schema(implementation = ResponseError.class))),
        @ApiResponse(
                responseCode = "404",
                description = "Cliente inesistente, o esistente ma di un altro utente",
                content = @Content(schema = @Schema(implementation = ResponseError.class)))
    })
    @PutMapping("/{id}")
    ResponseEntity<ClientDto> update(
            @Parameter(description = "Id del cliente") @PathVariable Long id, @Valid @RequestBody ClientDto dto);

    @Operation(summary = "Elenca i clienti", description = "Elenco paginato, filtrabile per i campi valorizzati in ClientDto.")
    @ApiResponse(responseCode = "200", description = "Pagina di risultati")
    @GetMapping
    ResponseEntity<ResponseList<ClientDto>> findAll(@ModelAttribute ClientDto filter, Pageable pageable);

    @Operation(summary = "Elimina un cliente")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Cliente eliminato"),
        @ApiResponse(
                responseCode = "404",
                description = "Cliente inesistente, o esistente ma di un altro utente",
                content = @Content(schema = @Schema(implementation = ResponseError.class)))
    })
    @DeleteMapping("/{id}")
    ResponseEntity<Void> delete(@Parameter(description = "Id del cliente") @PathVariable Long id);
}
