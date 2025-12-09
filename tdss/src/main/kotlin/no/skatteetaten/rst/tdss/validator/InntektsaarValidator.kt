package no.skatteetaten.rst.tdss.validator

import java.time.LocalDate
import no.skatteetaten.rst.domene.exception.TdssDomeneException

const val TIDLIGSTE_INNTEKTSAAR = 2000

/**
 *
 * Inntektsår leses fra oppgavens spesifikasjon
 */
object InntektsaarValidator {

    fun validerInntektsaar(spesifisertInntektsaar: Int): Int {
        val utledetInntektsaar = spesifisertInntektsaar.takeIf { it > 0 }
            ?: throw TdssDomeneException("Inntektsår må spesifiseres.")

        val naatidAar = LocalDate.now().year
        if (utledetInntektsaar !in TIDLIGSTE_INNTEKTSAAR..naatidAar) {
            throw TdssDomeneException(
                "Inntektsår: '$utledetInntektsaar' er ikke gyldig. Inntektsår må være spesifisert. Gyldige verdier: f.o.m. $TIDLIGSTE_INNTEKTSAAR t.o.m. $naatidAar."
            )
        }

        return utledetInntektsaar
    }
}
