package no.skatteetaten.rst.tdss.skattedata

import no.skatteetaten.rst.domene.exception.TdssDomeneException
import no.skatteetaten.rst.tdss.TdssInput

open class InntektsaarBuilder {
    @TdssInput
    var inntektsår: Int = 0
        set(value) {
            if (field != 0) {
                throw TdssDomeneException("Det er ikke gyldig å spesifisere flere inntektsår innen en blokk for en gitt datatype. Da må det spesifiseres som to distinkte blokker med hver sine inntektsår.")
            }
            field = value
        }
}