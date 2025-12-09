package no.skatteetaten.rst.tdss.person

import no.skatteetaten.rst.domene.Person
import no.skatteetaten.rst.domene.Skattedata
import no.skatteetaten.rst.tdss.TdssInput
import no.skatteetaten.rst.tdss.skattedata.saldorente.SaldoRenteBuilder

fun person(block: PersonBuilder.() -> Unit): Person = PersonBuilder().apply(block).build()

@TdssInput
open class PersonBuilder {
    private var skattedata = Skattedata()

    @TdssInput
    var fødselsnummer: String? = null

    @TdssInput
    fun saldoRente(block: SaldoRenteBuilder.() -> Unit) {
        skattedata.saldoRenter.add(SaldoRenteBuilder().apply(block).build())
    }

    fun build(): Person {
        return Person(fødselsnummer, skattedata)
    }
}