package no.skatteetaten.rst.tdss.skattedata.saldorente

import no.skatteetaten.rst.domene.grunnlagsdata.saldorente.Innhold
import no.skatteetaten.rst.domene.grunnlagsdata.saldorente.InnholdType
import no.skatteetaten.rst.tdss.TdssInput

@TdssInput
open class InnholdBuilder {
    @TdssInput
    var beløp: Long? = null

    fun lagSkattepliktigGevinst(): Innhold =
        Innhold(
            beløp,
            InnholdType.SKATTEPLIKTIG_GEVINST
        )

    fun lagFradragsberettigetTap(): Innhold =
        Innhold(
            beløp,
            InnholdType.FRADEAGSBERETTIGET_TAP
        )
}