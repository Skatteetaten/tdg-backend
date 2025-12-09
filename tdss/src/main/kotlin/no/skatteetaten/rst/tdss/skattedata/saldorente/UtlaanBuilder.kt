package no.skatteetaten.rst.tdss.skattedata.saldorente

import no.skatteetaten.rst.tdss.TdssInput

@TdssInput
class UtlaanBuilder : InnskuddUtlaanRenteBuilder() {

    internal val medlaantakerList = arrayListOf<String>()

    @TdssInput
    var medlåntakerFødselsnummer: String = "denne verdien brukes aldri"
        set(value) {
            medlaantakerList.add(value)
            field = value
        }

    @TdssInput
    var medlåntakerOrganisasjonsnummer: String = "denne verdien brukes aldri"
        set(value) {
            medlaantakerList.add(value)
            field = value
        }
}

