package no.skatteetaten.rst.testdatagenerator.dokument;

import java.util.List;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import no.skatteetaten.rst.domene.Part;
import no.skatteetaten.rst.testdatagenerator.dto.DokumentDto;
import no.skatteetaten.saldorente.ObjectFactory;
import no.skatteetaten.saldorente.Oppgaveeier;

/**
 *Genererer SaldoRente-dokumenter ved å kombinere domenetutfylling og XML-utfylling.
 */
@Component
@RequiredArgsConstructor
public class SaldoRenteGenerator {
    private static final ObjectFactory FACTORY = new ObjectFactory();

    private final SaldoRenteDomeneUtfyller domeneUtfyller;
    private final SaldoRenteDokumentUtfyller dokumentUtfyller;

    /**
     * Genererer XML-dokumenter for alle SaldoRente-oppgaver definert i en {@link Part}.
     *
     * @param part Domenemodell som inneholder SaldoRente-spesifikasjoner.
     * @return En liste av {@link DokumentDto} som representerer ferdige XML-dokumenter.
     */
    public List<DokumentDto> lagXmlDokumenter(Part part) {
        Oppgaveeier oppgaveeier = domeneUtfyller.fyllUtPart(part);

        return part.getSkattedata().getSaldoRenter().stream()
            .map(saldoRente -> dokumentUtfyller.lagMelding(saldoRente, oppgaveeier, domeneUtfyller.fyllUtOppgavegiver(saldoRente)))
            .map(meldingDto -> meldingDto.tilDokumentDto(FACTORY))
            .toList();
    }
}
