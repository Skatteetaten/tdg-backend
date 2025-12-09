package no.skatteetaten.rst.tdss.validator

import no.skatteetaten.rst.domene.exception.TdssDomeneException

/**
 * Fellesklasse for valideringer.
 */
object FellesValidator {
    private var orgnrPattern: Regex = Regex("^[0-9]{9}$")

    fun validerOrganisasjonsnummer(orgnummer: String): Boolean {
        return orgnrPattern.matches(orgnummer)
    }

    fun validerOrganisasjonsnummer(orgnummer: String?, feltnavn: String?) {
        if (orgnummer != null && !validerOrganisasjonsnummer(orgnummer))
            throw TdssDomeneException(String.format("%s må være et 9-sifret nummer", feltnavn))
    }
}