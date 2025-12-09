package no.skatteetaten.rst.tdss.skattedata.saldorente

import no.skatteetaten.FromVerdiEnum
import no.skatteetaten.rst.domene.exception.TdssDomeneException
import no.skatteetaten.rst.domene.grunnlagsdata.Valutakode
import no.skatteetaten.rst.domene.grunnlagsdata.saldorente.*
import no.skatteetaten.rst.tdss.TdssInput
import no.skatteetaten.rst.tdss.skattedata.InntektsaarBuilder
import no.skatteetaten.rst.tdss.skattedata.ValutakodeBuilder
import no.skatteetaten.rst.tdss.validator.FellesValidator
import no.skatteetaten.rst.tdss.validator.InntektsaarValidator.validerInntektsaar

@TdssInput
class SaldoRenteBuilder : InntektsaarBuilder() {
    private val innskuddUtlaanRenteListe = arrayListOf<InnskuddUtlaanRente>()
    private val medlaantakerListe = arrayListOf<String>()
    private var innholdsListe: ArrayList<Innhold> = arrayListOf()
    private var valutakode: Valutakode? = null

    @TdssInput
    var avsluttetKonto: Boolean? = null

    @TdssInput
    var disponentFødselsnummer: String? = null

    @TdssInput
    var opplysningspliktigOrganisasjonsnummer: String? = null

    @TdssInput
    fun innskudd(block: InnskuddUtlaanRenteBuilder.() -> Unit) {
        innskuddUtlaanRenteListe.add(InnskuddUtlaanRenteBuilder().apply(block).lagInnskuddUtlaanRente(InnskuddUtlaanRenteType.INNSKUDD))
    }

    @TdssInput
    fun utlån(block: UtlaanBuilder.() -> Unit) {
        val utlaanBuilder = UtlaanBuilder().apply(block)
        medlaantakerListe.addAll(utlaanBuilder.medlaantakerList)
        innskuddUtlaanRenteListe.add(utlaanBuilder.lagInnskuddUtlaanRente(InnskuddUtlaanRenteType.UTLAAN))
    }

    @TdssInput
    fun opptjenteRenter(block: InnskuddUtlaanRenteBuilder.() -> Unit) {
        innskuddUtlaanRenteListe.add(InnskuddUtlaanRenteBuilder().apply(block).lagInnskuddUtlaanRente(InnskuddUtlaanRenteType.OPPTJENTE_RENTER))
    }

    @TdssInput
    fun påløpteRenter(block: InnskuddUtlaanRenteBuilder.() -> Unit) {
        innskuddUtlaanRenteListe.add(InnskuddUtlaanRenteBuilder().apply(block).lagInnskuddUtlaanRente(InnskuddUtlaanRenteType.PAALOEPTE_RENTER))
    }

    @TdssInput
    fun betalteMisligholdteRenter(block: InnskuddUtlaanRenteBuilder.() -> Unit) {
        innskuddUtlaanRenteListe.add(
            InnskuddUtlaanRenteBuilder().apply(block).lagInnskuddUtlaanRente(InnskuddUtlaanRenteType.BETALTE_MISLIGHOLDTE_RENTER)
        )
    }

    @TdssInput
    fun tilbakebetalteMisligholdteRenter(block: InnskuddUtlaanRenteBuilder.() -> Unit) {
        innskuddUtlaanRenteListe.add(
            InnskuddUtlaanRenteBuilder().apply(block).lagInnskuddUtlaanRente(InnskuddUtlaanRenteType.TILBAKEBETALTE_MISLIGHOLDTE_RENTER)
        )
    }

    var saldoRenteKontoType: SaldoRenteKontotype? = null

    @TdssInput
    fun kontoType(block: SaldoRenteKontoTypeBuilder.() -> Unit) {
        saldoRenteKontoType = SaldoRenteKontoTypeBuilder().apply(block).build()
    }

    var saldoRenteSkattekontoEgnethet: SaldoRenteSkattekontoEgnethet? = null

    @TdssInput
    var skattekontoEgnethet: Int? = null

    @TdssInput
    fun skattepliktigGevinst(block: InnholdBuilder.() -> Unit) {
        if (innholdsListe.any { innhold: Innhold -> innhold.innholdType == InnholdType.SKATTEPLIKTIG_GEVINST }) {
            throw TdssDomeneException("saldoRente: skattepliktigGevinst skal kun oppgis én gang")
        }
        innholdsListe.add(InnholdBuilder().apply(block).lagSkattepliktigGevinst())
    }

    @TdssInput
    fun fradragsberettigetTap(block: InnholdBuilder.() -> Unit) {
        if (innholdsListe.any { innhold: Innhold -> innhold.innholdType == InnholdType.FRADEAGSBERETTIGET_TAP }) {
            throw TdssDomeneException("saldoRente: fradragsberettigetTap skal kun oppgis én gang")
        }
        innholdsListe.add(InnholdBuilder().apply(block).lagFradragsberettigetTap())
    }

    @TdssInput
    fun valutakode(block: ValutakodeBuilder.() -> Unit) {
        if (valutakode != null) {
            throw TdssDomeneException("saldoRente: valutakode skal kun oppgis én gang")
        }
        valutakode = ValutakodeBuilder().apply(block).build()
        if (valutakode == Valutakode.IKKE_SPESIFISERT) {
            valutakode = Valutakode.USD
        }
    }

    fun build(): SaldoRente {
        val intAar = validerInntektsaar(inntektsår)

        if (innskuddUtlaanRenteListe.isEmpty() && avsluttetKonto != true) {
            throw TdssDomeneException("saldoRente: innskudd, utlån eller renter må spesifiseres.")
        }

        if (innholdsListe
                .map { innhold: Innhold -> innhold.innholdType }
                .containsAll(listOf(InnholdType.SKATTEPLIKTIG_GEVINST, InnholdType.FRADEAGSBERETTIGET_TAP))
        ) {
            throw TdssDomeneException("saldoRente: fradragsberettigetTap og skattepliktigGevinst kan ikke kombineres for samme konto")
        }

        val set = kotlin.collections.HashSet<InnskuddUtlaanRenteType>()
        for (innskudd in innskuddUtlaanRenteListe) {
            if (!set.add(innskudd.innskuddUtlaanRenteType)) {
                throw TdssDomeneException("saldoRente: kan ikke ha duplikate innskudd, utlån, eller renter av type: " + innskudd.innskuddUtlaanRenteType)
            }
        }
        saldoRenteSkattekontoEgnethet = validerSkattekontoEgnethet(this.skattekontoEgnethet)

        FellesValidator.validerOrganisasjonsnummer(
            this.opplysningspliktigOrganisasjonsnummer,
            "selskapsmelding: spesifisert organisasjonsnummer '" + this.opplysningspliktigOrganisasjonsnummer + "'"
        )

        val saldoRente = SaldoRente(
            intAar,
            innskuddUtlaanRenteListe,
            saldoRenteKontoType,
            avsluttetKonto,
            saldoRenteSkattekontoEgnethet,
            disponentFødselsnummer,
            opplysningspliktigOrganisasjonsnummer
        )

        saldoRente.medlaantakerIdentifikatorListe.addAll(medlaantakerListe)
        innholdsListe.isNotEmpty().let { saldoRente.gevinstTapInnholdListe.addAll(innholdsListe) }

        if (valutakode != null) {
            saldoRente.valutakode = valutakode
        }

        return saldoRente
    }

    private fun validerSkattekontoEgnethet(skattekontoEgnethet: Int?): SaldoRenteSkattekontoEgnethet? {
        if (null == skattekontoEgnethet) {
            return null
        }
        return FromVerdiEnum.fromVerdi(SaldoRenteSkattekontoEgnethet::class.java, skattekontoEgnethet.toString())
    }
}