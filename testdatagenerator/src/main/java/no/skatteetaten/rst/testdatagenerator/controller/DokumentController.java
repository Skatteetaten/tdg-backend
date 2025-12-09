package no.skatteetaten.rst.testdatagenerator.controller;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.servers.Server;
import lombok.RequiredArgsConstructor;
import no.skatteetaten.rst.testdatagenerator.dto.DokumentDto;
import no.skatteetaten.rst.testdatagenerator.dto.SpesifikasjonDto;
import no.skatteetaten.rst.testdatagenerator.service.DokumentService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/dokumenter")
@OpenAPIDefinition(servers = { @Server(url = "/", description = "Default Server URL") })
public class DokumentController {
    private static final Logger LOG = LoggerFactory.getLogger(DokumentController.class);
    private final DokumentService dokumentService;

    @Operation(summary = "Send inn testdata spesifikasjonsspråk (TDSS)",
        description = "Tjenesten benyttes for å sende inn TDSS, et språk utviklet som en intern DSL i Kotlin.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Spesifikasjon mottatt. Returnerer unik korrelasjonsId for denne innsendingen")
    })
    @PostMapping("/tdss")
    public ResponseEntity<String> sendInnTdss(
        @RequestBody
        @Parameter(description = "JSON-objekt bestående av spesifikasjon.", required = true)
        SpesifikasjonDto spesifikasjonDto
    ) {
        String korrelasjonsId = UUID.randomUUID().toString();
        LOG.info("Mottatt TDSS med korrelasjonsId={}", korrelasjonsId);

        dokumentService.leggTilDokumenter(korrelasjonsId, spesifikasjonDto.getSpesifikasjon());

        URI ressursUri = ServletUriComponentsBuilder
            .fromCurrentRequest()
            .path("/{korrelasjonsId}")
            .buildAndExpand(korrelasjonsId)
            .toUri();

        return ResponseEntity.created(ressursUri).body(korrelasjonsId);
    }

    @Operation(summary = "Hent dokumenter med gitt korrelasjonsId",
        description = "Tjenesten benyttes for å hente ut xml-dokumenter som er behandlet av testdatageneratoren. Bruk korrelasjonsId for å hente riktige dokumenter.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "korrelasjonsId mottatt. Returnerer dokumenter konstruert fra tidligere innsendt spesifikasjon")
    })
    @GetMapping("/{korrelasjonsId}")
    public ResponseEntity<List<DokumentDto>> hentDokumenter(
        @PathVariable
        String korrelasjonsId
    ) {
        LOG.info("Henter ut dokumenter med korrelasjonsId={}", korrelasjonsId);
        return ResponseEntity.ok(dokumentService.hentDokumenter(korrelasjonsId));
    }

}
