package no.skatteetaten.rst.testdatagenerator.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import no.skatteetaten.rst.testdatagenerator.dto.DokumentDto;

@SpringBootTest
class DokumentServiceTest {

    @Autowired
    private DokumentService dokumentService;

    @Test
    void genererMeldingFraPart() {
        String tdss = "person { fødselsnummer = \"26890296253\" ; "
            + "saldoRente { inntektsår = 2023 ; innskudd { beløp = 1201 } } ; "
            + "saldoRente { inntektsår = 2024 ; innskudd { beløp = 1302 } } }";

        dokumentService.leggTilDokumenter("123", tdss);
        List<DokumentDto> xmlDokumenter = dokumentService.hentDokumenter("123");

        assertEquals(2, xmlDokumenter.size());
        assertTrue(xmlDokumenter.getFirst().dokument().contains("1201"));
        assertTrue(xmlDokumenter.getLast().dokument().contains("1302"));
    }

    @Test
    void genererMeldingFraPartUtenFnr() {
        String tdss = "person { saldoRente { inntektsår = 2024 ; innskudd { beløp = 1201 } } ; "
            + "saldoRente { inntektsår = 2025 ; innskudd { beløp = 1302 } } }";

        dokumentService.leggTilDokumenter("123", tdss);
        List<DokumentDto> xmlDokumenter = dokumentService.hentDokumenter("123");

        assertEquals(2, xmlDokumenter.size());
        assertTrue(xmlDokumenter.getFirst().dokument().contains("1201"));
        assertTrue(xmlDokumenter.getLast().dokument().contains("1302"));
    }

    @Test
    void genererMeldingFraPartForEnhet() {
        String tdss = "enhet { saldoRente { inntektsår = 2024 ; innskudd { beløp = 1201 } } ; saldoRente { inntektsår = 2024 ; innskudd { beløp = 1302 } } }";

        dokumentService.leggTilDokumenter("123", tdss);
        List<DokumentDto> xmlDokumenter = dokumentService.hentDokumenter("123");

        assertEquals(2, xmlDokumenter.size());
        assertTrue(xmlDokumenter.getFirst().dokument().contains("1201"));
        assertTrue(xmlDokumenter.getLast().dokument().contains("1302"));
    }

}
