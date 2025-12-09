package no.skatteetaten.rst.testdatagenerator.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import lombok.RequiredArgsConstructor;
import no.skatteetaten.rst.domene.Part;
import no.skatteetaten.rst.testdatagenerator.dokument.SaldoRenteGenerator;
import no.skatteetaten.rst.testdatagenerator.dto.DokumentDto;
import no.skatteetaten.rst.testdatagenerator.parser.TdssParser;

/**
 * Tjeneste for håndtering av dokumentgenerering.
 * <p>
 * Denne tjenesten koordinerer prosessen fra mottak av en TDSS-spesifikasjon,
 * via parsing og generering, til lagring og uthenting av de ferdige XML-dokumentene.
 */
@Service
@RequiredArgsConstructor
public class DokumentService {
    private final Map<String, List<DokumentDto>> dokumentMap = new HashMap<>();
    private final TdssParser tdssParser;
    private final SaldoRenteGenerator saldoRenteGenerator;

    /**
     * Parser en TDSS-spesifikasjon, genererer XML-dokumenter og lagrer dokumentene i minnet.
     *
     * @param korrelasjonsId Unik identifikator for denne bestillingen.
     * @param spesifikasjon  Streng som inneholder testdata-spesifikasjonen (TDSS).
     */
    public void leggTilDokumenter(String korrelasjonsId, String spesifikasjon) {
        Part part = tdssParser.opprettPartFraTdss(spesifikasjon);
        List<DokumentDto> dokumentsett = saldoRenteGenerator.lagXmlDokumenter(part);
        dokumentMap.put(korrelasjonsId, dokumentsett);
    }

    /**
     * Henter en liste med genererte dokumenter basert på korrelasjonsId.
     *
     * @param korrelasjonsId Den unike identifikatoren for bestillingen.
     * @return En liste med {@link DokumentDto} som inneholder generert XML.
     * @throws ResponseStatusException hvis ingen dokumenter finnes for gitt korrelasjonsId (404 NOT FOUND).
     */
    public List<DokumentDto> hentDokumenter(String korrelasjonsId) {
        if (dokumentMap.containsKey(korrelasjonsId)) {
            return dokumentMap.get(korrelasjonsId);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Fant ikke dokumenter for korrelasjonsId: " + korrelasjonsId);
        }
    }
}
