package no.skatteetaten.rst.testdatagenerator.dokument;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import no.skatteetaten.rst.domene.Part;
import no.skatteetaten.rst.testdatagenerator.dto.DokumentDto;
import no.skatteetaten.rst.testdatagenerator.parser.TdssParser;

@SpringBootTest
public class SaldoRenteGeneratorTest {

    @Autowired
    private SaldoRenteGenerator saldoRenteGenerator;

    @Test
    void lagXmlDokumenter() {
        final long beloep = 1201;
        String tdss = "person { fødselsnummer = \"26890296253\" ; saldoRente { inntektsår = 2024 ; innskudd { beløp = " + beloep + " } } }";
        TdssParser tdssParser = new TdssParser();
        Part part = tdssParser.opprettPartFraTdss(tdss);

        List<DokumentDto> dokumentDtoer = saldoRenteGenerator.lagXmlDokumenter(part);

        assertTrue(dokumentDtoer.getFirst().dokument().contains("26890296253"));
    }
}
