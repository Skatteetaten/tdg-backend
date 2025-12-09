package no.skatteetaten.rst.testdatagenerator.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigInteger;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import no.skatteetaten.rst.domene.Part;
import no.skatteetaten.rst.domene.grunnlagsdata.saldorente.InnskuddUtlaanRenteType;

@SpringBootTest
public class TdssParserTest {

    @Test
    void parseTdssTilPart() {
        final long beloep = 1201;
        final int inntektsaar = 2024;

        String tdss = "person { fødselsnummer = \"26890296253\" ; saldoRente { inntektsår = " + inntektsaar + " ; innskudd { beløp = " + beloep + " } } }";
        TdssParser tdssParser = new TdssParser();
        Part part = tdssParser.opprettPartFraTdss(tdss);

        assertEquals("26890296253", part.getIdentifikatornummer());
        assertEquals(inntektsaar, part.getSkattedata().getSaldoRenter().getFirst().getInntektsaar());
        assertEquals(InnskuddUtlaanRenteType.INNSKUDD, part.getSkattedata().getSaldoRenter().getFirst().getInnskuddUtlaanRenteListe().getFirst().getInnskuddUtlaanRenteType());
        assertEquals(BigInteger.valueOf(beloep), part.getSkattedata().getSaldoRenter().getFirst().getInnskuddUtlaanRenteListe().getFirst().getBeloep());
    }
}
