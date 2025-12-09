package no.skatteetaten.rst.tdss.enhet

import no.skatteetaten.rst.domene.Enhet
import no.skatteetaten.rst.domene.Skattedata
import no.skatteetaten.rst.tdss.TdssInput
import no.skatteetaten.rst.tdss.skattedata.saldorente.SaldoRenteBuilder

fun enhet(block: EnhetBuilder.() -> Unit): Enhet = EnhetBuilder().apply(block).build()

@TdssInput
class EnhetBuilder {
    private var skattedata = Skattedata()

    @TdssInput
    var organisasjonsnummer: String? = null

    @TdssInput
    fun saldoRente(block: SaldoRenteBuilder.() -> Unit) {
        skattedata.saldoRenter.add(SaldoRenteBuilder().apply(block).build())
    }

    fun build(): Enhet {
        return Enhet(organisasjonsnummer, skattedata)
    }
}