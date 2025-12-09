package no.skatteetaten.rst.testdatagenerator.tenor;

import java.util.Optional;

public interface TenorKlient {
    Optional<TenorPart> hentPerson(String foedselsnummer);

    Optional<TenorPart> hentEnhet(String organisasjonsnummer);

    TenorPart hentTilfeldigPart(PartType partType);
}
