package no.skatteetaten.rst.tdss.skattedata.saldorente

import no.skatteetaten.rst.domene.grunnlagsdata.saldorente.SaldoRenteKontotype
import no.skatteetaten.rst.tdss.TdssInput

@TdssInput
class SaldoRenteKontoTypeBuilder {
    private var kontotype: SaldoRenteKontotype? = null

    @TdssInput
    val innskuddskontoINok: Unit
        get() {
            kontotype = SaldoRenteKontotype.INNSKUDDSKONTO_I_NOK
        }

    @TdssInput
    val innskuddskontoIUtenlandskValuta: Unit
        get() {
            kontotype = SaldoRenteKontotype.INNSKUDDSKONTO_I_UTENLANDSK_VALUTA
        }

    fun build(): SaldoRenteKontotype? {
        return kontotype
    }
}