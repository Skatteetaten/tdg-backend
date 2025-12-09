package no.skatteetaten.rst.testdatagenerator.tenor;

import java.util.Optional;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("prod")
public class TenorKlientMaskinport implements TenorKlient {

    @Override
    public Optional<TenorPart> hentEnhet(String organisasjonsnummer) {
        return Optional.empty();
    }

    @Override
    public Optional<TenorPart> hentPerson(String foedselsnummer) {
        return Optional.empty();
    }

    @Override
    public TenorPart hentTilfeldigPart(PartType partType) {
        return null;
    }
}
