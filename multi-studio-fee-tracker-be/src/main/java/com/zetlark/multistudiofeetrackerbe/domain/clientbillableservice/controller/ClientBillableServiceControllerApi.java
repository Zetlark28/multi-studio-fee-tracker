package com.zetlark.multistudiofeetrackerbe.domain.clientbillableservice.controller;

import com.zetlark.multistudiofeetrackerbe.application.common.dto.ResponseList;
import com.zetlark.multistudiofeetrackerbe.application.common.exception.ResponseError;
import com.zetlark.multistudiofeetrackerbe.domain.clientbillableservice.dto.ClientBillableServiceDto;
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

@Tag(name = "Servizi fatturabili", description = "Servizi offerti da un cliente (usati dalle attività di tipo PERCENT).")
@ApiResponses({
    @ApiResponse(
            responseCode = "401",
            description = "Token mancante o non valido",
            content = @Content(schema = @Schema(implementation = ResponseError.class)))
})
@RequestMapping("/client-billable-services")
public interface ClientBillableServiceControllerApi {

    @Operation(summary = "Crea un servizio fatturabile")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Servizio creato"),
        @ApiResponse(
                responseCode = "400",
                description = "Dati non validi",
                content = @Content(schema = @Schema(implementation = ResponseError.class)))
    })
    @PostMapping
    ResponseEntity<ClientBillableServiceDto> create(@Valid @RequestBody ClientBillableServiceDto dto);

    @Operation(summary = "Dettaglio di un servizio fatturabile")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Servizio trovato"),
        @ApiResponse(
                responseCode = "404",
                description = "Servizio inesistente, o esistente ma di un altro utente",
                content = @Content(schema = @Schema(implementation = ResponseError.class)))
    })
    @GetMapping("/{id}")
    ResponseEntity<ClientBillableServiceDto> getById(@Parameter(description = "Id del servizio") @PathVariable Long id);

    @Operation(summary = "Aggiorna un servizio fatturabile")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Servizio aggiornato"),
        @ApiResponse(
                responseCode = "400",
                description = "Dati non validi",
                content = @Content(schema = @Schema(implementation = ResponseError.class))),
        @ApiResponse(
                responseCode = "404",
                description = "Servizio inesistente, o esistente ma di un altro utente",
                content = @Content(schema = @Schema(implementation = ResponseError.class)))
    })
    @PutMapping("/{id}")
    ResponseEntity<ClientBillableServiceDto> update(
            @Parameter(description = "Id del servizio") @PathVariable Long id,
            @Valid @RequestBody ClientBillableServiceDto dto);

    @Operation(
            summary = "Elenca i servizi fatturabili",
            description = "Elenco paginato, filtrabile per i campi valorizzati in ClientBillableServiceDto.")
    @ApiResponse(responseCode = "200", description = "Pagina di risultati")
    @GetMapping
    ResponseEntity<ResponseList<ClientBillableServiceDto>> findAll(
            @ModelAttribute ClientBillableServiceDto filter, Pageable pageable);

    @Operation(summary = "Elimina un servizio fatturabile")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Servizio eliminato"),
        @ApiResponse(
                responseCode = "404",
                description = "Servizio inesistente, o esistente ma di un altro utente",
                content = @Content(schema = @Schema(implementation = ResponseError.class)))
    })
    @DeleteMapping("/{id}")
    ResponseEntity<Void> delete(@Parameter(description = "Id del servizio") @PathVariable Long id);
}
