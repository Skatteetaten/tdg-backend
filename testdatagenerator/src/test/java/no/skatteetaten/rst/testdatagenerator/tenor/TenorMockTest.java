package no.skatteetaten.rst.testdatagenerator.tenor;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class TenorMockTest {

    @Autowired
    private TenorKlient tenorMock;

    @Test
    void testHentPersonMedGyldigFnr() {
        Optional<TenorPart> person = tenorMock.hentPerson("26890296253");
        assertThat(person).isPresent();
        assertThat(person.get().navn()).isEqualTo("Bra Gitar");
    }

    @Test
    void testHentEnhetMedGyldigOrgnr() {
        Optional<TenorPart> enhet = tenorMock.hentEnhet("314322045");
        assertThat(enhet).isPresent();
        assertThat(enhet.get().navn()).isEqualTo("NY UPRAKTISK KATT KLAPPSTOL");
    }

    @Test
    void testHentPersonMedUgyldigFnr() {
        Optional<TenorPart> person = tenorMock.hentPerson("12345678910");
        assertThat(person).isEmpty();
    }

    @Test
    void testHentEnhetMedUgyldigOrgnr() {
        Optional<TenorPart> enhet = tenorMock.hentEnhet("333333333");
        assertThat(enhet).isEmpty();
    }

    @Test
    void testHentTilfeldigPartPerson() {
        TenorPart tilfeldigPerson = tenorMock.hentTilfeldigPart(PartType.PERSON);
        assertThat(tilfeldigPerson).isNotNull();
        final int foedselsnrDnrLengde = 11;
        assertThat(tilfeldigPerson.identifikator()).hasSize(foedselsnrDnrLengde);
        assertThat(tilfeldigPerson.navn()).isNotEmpty();
    }

    @Test
    void testHentTilfeldigPartEnhet() {
        TenorPart tilfeldigEnhet = tenorMock.hentTilfeldigPart(PartType.ENHET);
        assertThat(tilfeldigEnhet).isNotNull();
        assertThat(tilfeldigEnhet.identifikator()).hasSize(9);
        assertThat(tilfeldigEnhet.navn()).isNotEmpty();
    }
}
