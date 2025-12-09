package no.skatteetaten.rst.tdss.skattedata

import no.skatteetaten.rst.domene.grunnlagsdata.Valutakode
import no.skatteetaten.rst.tdss.TdssInput

@TdssInput
class ValutakodeBuilder {
    var valutakode: Valutakode? = null

    @TdssInput
    val AUD: Unit
        get() {
            valutakode = Valutakode.AUD
        }

    @TdssInput
    val CAD: Unit
        get() {
            valutakode = Valutakode.CAD
        }

    @TdssInput
    val CHF: Unit
        get() {
            valutakode = Valutakode.CHF
        }

    @TdssInput
    val CNY: Unit
        get() {
            valutakode = Valutakode.CNY
        }

    @TdssInput
    val DKK: Unit
        get() {
            valutakode = Valutakode.DKK
        }

    @TdssInput
    val EUR: Unit
        get() {
            valutakode = Valutakode.EUR
        }

    @TdssInput
    val GBP: Unit
        get() {
            valutakode = Valutakode.GBP
        }

    @TdssInput
    val HKD: Unit
        get() {
            valutakode = Valutakode.HKD
        }

    @TdssInput
    val JPY: Unit
        get() {
            valutakode = Valutakode.JPY
        }

    @TdssInput
    val NOK: Unit
        get() {
            valutakode = Valutakode.NOK
        }

    @TdssInput
    val SEK: Unit
        get() {
            valutakode = Valutakode.SEK
        }

    @TdssInput
    val SGD: Unit
        get() {
            valutakode = Valutakode.SGD
        }

    @TdssInput
    val USD: Unit
        get() {
            valutakode = Valutakode.USD
        }

    @TdssInput
    val XAG: Unit
        get() {
            valutakode = Valutakode.XAG
        }

    @TdssInput
    val XAU: Unit
        get() {
            valutakode = Valutakode.XAU
        }

    fun build(): Valutakode {
        if (valutakode == null) {
            valutakode = Valutakode.IKKE_SPESIFISERT
        }
        return valutakode!!
    }
}