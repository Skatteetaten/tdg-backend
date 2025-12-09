package no.skatteetaten.rst.tdss.skattedata.saldorente

import no.skatteetaten.rst.domene.grunnlagsdata.saldorente.InnskuddUtlaanRente
import no.skatteetaten.rst.domene.grunnlagsdata.saldorente.InnskuddUtlaanRenteType
import no.skatteetaten.rst.tdss.TdssInput

@TdssInput
open class InnskuddUtlaanRenteBuilder {
    @TdssInput
    var beløp: Int? = null

    fun lagInnskuddUtlaanRente(innskuddUtlaanRenteType: InnskuddUtlaanRenteType): InnskuddUtlaanRente =
        InnskuddUtlaanRente(
            beløp?.toBigInteger(),
            innskuddUtlaanRenteType
        )
}