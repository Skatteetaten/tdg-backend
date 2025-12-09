package no.skatteetaten.rst.tdss.skattedata.saldorente

import java.time.LocalDateTime
import no.skatteetaten.rst.domene.grunnlagsdata.Valutakode
import no.skatteetaten.rst.domene.grunnlagsdata.saldorente.InnholdType
import no.skatteetaten.rst.domene.grunnlagsdata.saldorente.InnskuddUtlaanRenteType
import no.skatteetaten.rst.domene.grunnlagsdata.saldorente.SaldoRenteKontotype
import no.skatteetaten.rst.domene.grunnlagsdata.saldorente.SaldoRenteSkattekontoEgnethet
import no.skatteetaten.rst.tdss.person.person
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import no.skatteetaten.rst.domene.exception.TdssDomeneException
import no.skatteetaten.rst.tdss.validator.TIDLIGSTE_INNTEKTSAAR

class SaldorenteBuilderTest {
    private val sistMuligAar = LocalDateTime.now().year

    @Test
    fun toSaldoRenteFunksjoner() {
        val person = person {
            saldoRente {
                inntektsår = sistMuligAar
                innskudd {
                    beløp = 300_000
                }
            }
            saldoRente {
                inntektsår = sistMuligAar - 1
                innskudd {
                    beløp = 400_000
                }
            }
        }

        val saldoRentesistMuligAar = person.skattedata.saldoRenter[0]
        val innskudd1 = saldoRentesistMuligAar.innskuddUtlaanRenteListe[0]
        assertEquals(InnskuddUtlaanRenteType.INNSKUDD, innskudd1.innskuddUtlaanRenteType)
        assertEquals(300_000.toBigInteger(), innskudd1.beloep)
        assertEquals(sistMuligAar, saldoRentesistMuligAar.inntektsaar)
        assertTrue(saldoRentesistMuligAar.gevinstTapInnholdListe.isEmpty())
        assertNull(saldoRentesistMuligAar.valutakode)

        val saldoRenteNaavaerendeAar = person.skattedata.saldoRenter[1]
        val innskudd2 = saldoRenteNaavaerendeAar.innskuddUtlaanRenteListe[0]
        assertEquals(InnskuddUtlaanRenteType.INNSKUDD, innskudd2.innskuddUtlaanRenteType)
        assertEquals(400_000.toBigInteger(), innskudd2.beloep)
        assertEquals(sistMuligAar - 1, saldoRenteNaavaerendeAar.inntektsaar)
        assertTrue(saldoRenteNaavaerendeAar.gevinstTapInnholdListe.isEmpty())
        assertNull(saldoRenteNaavaerendeAar.valutakode)
    }

    @Test
    fun innskuddOgRenterTest() {
        val person = person {
            saldoRente {
                inntektsår = sistMuligAar
                innskudd {
                    beløp = 400_000
                }
                opptjenteRenter {
                    beløp = 4_000
                }
            }
        }

        val saldoRenter = person.skattedata.saldoRenter
        assertNotNull(saldoRenter)
        assertEquals(2, saldoRenter[0].innskuddUtlaanRenteListe.size)

        val saldoRente = saldoRenter[0]
        assertEquals(sistMuligAar, saldoRente.inntektsaar)

        val innskudd = saldoRente.innskuddUtlaanRenteListe[0]
        assertEquals(InnskuddUtlaanRenteType.INNSKUDD, innskudd.innskuddUtlaanRenteType)
        assertEquals(400_000.toBigInteger(), innskudd.beloep)

        assertTrue(saldoRente.gevinstTapInnholdListe.isEmpty())
        assertNull(saldoRente.valutakode)
    }

    @Test
    fun laanOgRenter() {
        val person = person {
            saldoRente {
                inntektsår = sistMuligAar - 1
                utlån {
                    beløp = 600_000
                }
                opptjenteRenter { }
                påløpteRenter { }
            }
        }

        val saldoRenter = person.skattedata.saldoRenter
        assertNotNull(saldoRenter)
        assertEquals(3, saldoRenter[0].innskuddUtlaanRenteListe.size)

        val saldoRente = saldoRenter[0]
        assertEquals(sistMuligAar - 1, saldoRente.inntektsaar)

        val laan = saldoRente.innskuddUtlaanRenteListe[0]
        assertEquals(InnskuddUtlaanRenteType.UTLAAN, laan.innskuddUtlaanRenteType)
        assertEquals(600_000.toBigInteger(), laan.beloep)

        val paalopteRenter = saldoRente.innskuddUtlaanRenteListe[1]
        assertEquals(InnskuddUtlaanRenteType.OPPTJENTE_RENTER, paalopteRenter.innskuddUtlaanRenteType)
        assertNull(paalopteRenter.beloep)

        val opptjenteRenter = saldoRente.innskuddUtlaanRenteListe[2]
        assertEquals(InnskuddUtlaanRenteType.PAALOEPTE_RENTER, opptjenteRenter.innskuddUtlaanRenteType)
        assertNull(opptjenteRenter.beloep)

        assertTrue(saldoRente.gevinstTapInnholdListe.isEmpty())
        assertNull(saldoRente.valutakode)
    }

    @Test
    fun saldorenteTestUtenInnskuddUtlaanRente() {
        val exception = assertThrows< TdssDomeneException> {
            person {
                saldoRente {
                    inntektsår = sistMuligAar
                }
            }
        }

        assertEquals("saldoRente: innskudd, utlån eller renter må spesifiseres.", exception.message)
    }

    @Test
    fun saldorenteTestMedDuplikateInnskuddUtlaanRente() {
        val exception = assertThrows< TdssDomeneException> {
            person {
                saldoRente {
                    inntektsår = sistMuligAar
                    utlån {
                        beløp = 600_000
                    }
                    utlån { }
                }
            }
        }

        assertEquals(
            "saldoRente: kan ikke ha duplikate innskudd, utlån, eller renter av type: UTLAAN",
            exception.message
        )
    }

    @Test
    fun saldorenteTestFeilInntektsaarForLav() {
        val inntektsaar = TIDLIGSTE_INNTEKTSAAR - 1

        val exception = assertThrows< TdssDomeneException> {
            person {
                saldoRente {
                    inntektsår = inntektsaar
                    utlån {
                        beløp = 600_000
                    }
                }
            }
        }

        assertEquals(
            "Inntektsår: '$inntektsaar' er ikke gyldig. Inntektsår må være spesifisert. Gyldige verdier: f.o.m. $TIDLIGSTE_INNTEKTSAAR t.o.m. $sistMuligAar.",
            exception.message
        )
    }

    @Test
    fun saldorenteTestFeilInntektsaarForhoey() {
        val inntektsaar = sistMuligAar + 1

        val exception = assertThrows< TdssDomeneException> {
            person {
                saldoRente {
                    inntektsår = inntektsaar
                    utlån {
                        beløp = 600_000
                    }
                }
            }
        }

        assertEquals(
            "Inntektsår: '$inntektsaar' er ikke gyldig. Inntektsår må være spesifisert. Gyldige verdier: f.o.m. $TIDLIGSTE_INNTEKTSAAR t.o.m. $sistMuligAar.",
            exception.message
        )
    }

    @Test
    fun saldorenteTestFeilInntektsaarNull() {
        val exception = assertThrows< TdssDomeneException> {
            person {
                saldoRente {
                    utlån {
                        beløp = 600_000
                    }
                }
            }
        }

        assertEquals(
            "Inntektsår må spesifiseres.",
            exception.message
        )
    }

    @Test
    fun saldoRenteKontotypeHappyDay() {
        val person = person {
            saldoRente {
                inntektsår = sistMuligAar - 1
                utlån {
                    beløp = 600_000
                }
                avsluttetKonto = false
                kontoType {
                    innskuddskontoINok
                }
                skattekontoEgnethet = 1
            }
        }

        val saldoRenter = person.skattedata.saldoRenter
        assertNotNull(saldoRenter)
        assertEquals(1, saldoRenter[0].innskuddUtlaanRenteListe.size)

        val saldoRente = saldoRenter[0]
        assertEquals(sistMuligAar - 1, saldoRente.inntektsaar)

        val innskudd = saldoRente.innskuddUtlaanRenteListe[0]
        assertEquals(InnskuddUtlaanRenteType.UTLAAN, innskudd.innskuddUtlaanRenteType)
        assertEquals(600_000.toBigInteger(), innskudd.beloep)

        assertFalse(saldoRente.isAvsluttetKonto)
        assertEquals(SaldoRenteKontotype.INNSKUDDSKONTO_I_NOK, saldoRente.saldoRenteKontotype)
        assertEquals(SaldoRenteSkattekontoEgnethet.SALDO_RENTE_SKATTEKONTO_EGNETHET_1, saldoRente.saldoRenteSkattekontoEgnethet)
        assertTrue(saldoRente.gevinstTapInnholdListe.isEmpty())
        assertNull(saldoRente.valutakode)
    }

    @Test
    fun saldoRenteSkattekontoEgnethetHappyDay() {
        val inntektsaar = sistMuligAar - 1

        val person = person {
            saldoRente {
                inntektsår = inntektsaar
                utlån {
                    beløp = 600_000
                }
                avsluttetKonto = true
                kontoType {
                    innskuddskontoINok
                }
                skattekontoEgnethet = 1
            }
        }

        val saldoRenter = person.skattedata.saldoRenter
        assertNotNull(saldoRenter)
        assertEquals(1, saldoRenter[0].innskuddUtlaanRenteListe.size)

        val saldoRente = saldoRenter[0]
        assertEquals(inntektsaar, saldoRente.inntektsaar)

        val innskudd = saldoRente.innskuddUtlaanRenteListe[0]
        assertEquals(InnskuddUtlaanRenteType.UTLAAN, innskudd.innskuddUtlaanRenteType)
        assertEquals(600_000.toBigInteger(), innskudd.beloep)

        assertTrue(saldoRente.isAvsluttetKonto)
        assertEquals(SaldoRenteKontotype.INNSKUDDSKONTO_I_NOK, saldoRente.saldoRenteKontotype)
        assertEquals(SaldoRenteSkattekontoEgnethet.SALDO_RENTE_SKATTEKONTO_EGNETHET_1, saldoRente.saldoRenteSkattekontoEgnethet)
        assertTrue(saldoRente.gevinstTapInnholdListe.isEmpty())
        assertNull(saldoRente.valutakode)
    }

    @Test
    fun saldorenteValiderSkattekontoEgnethet() {
        val exception = assertThrows<IllegalArgumentException> {
            person {
                saldoRente {
                    kontoType {
                        innskuddskontoIUtenlandskValuta
                    }
                    skattekontoEgnethet = 4
                    inntektsår = sistMuligAar - 1
                    utlån {
                        beløp = 600_000
                    }
                }
            }
        }

        assertEquals("Det finnes ingen type med verdi 4 i enumen SaldoRenteSkattekontoEgnethet", exception.message)
    }

    @Test
    fun saldorenteTestAvsluttetKontoUtenInnskuddUtlaanRente() {
        val person = person {
            saldoRente {
                inntektsår = sistMuligAar
                avsluttetKonto = true
            }
        }

        val saldoRente = person.skattedata.saldoRenter[0]
        assertEquals(sistMuligAar, saldoRente.inntektsaar)
        assertTrue(saldoRente.isAvsluttetKonto)
    }

    @Test
    fun saldorenteTestUtenDisponentFoedselsnummer() {
        val person = person {
            saldoRente {
                inntektsår = sistMuligAar
                avsluttetKonto = true
            }
        }

        val saldoRente = person.skattedata.saldoRenter[0]
        assertNull(saldoRente.disponentFoedselsnummer)
    }

    @Test
    fun saldorenteTestMedDisponentFoedselsnummer() {
        val person = person {
            saldoRente {
                inntektsår = sistMuligAar
                avsluttetKonto = true
                disponentFødselsnummer = "128765384"
            }
        }

        val saldoRente = person.skattedata.saldoRenter[0]
        assertEquals("128765384", saldoRente.disponentFoedselsnummer)
    }

    @Test
    fun saldoRenteMedlaantakere() {
        val person = person {
            saldoRente {
                inntektsår = sistMuligAar
                utlån {
                    beløp = 700_000
                    medlåntakerFødselsnummer = "123"
                    medlåntakerFødselsnummer = "456"
                    medlåntakerOrganisasjonsnummer = "789"
                    medlåntakerOrganisasjonsnummer = "10"
                }
            }
        }

        val medlaantakere = person.skattedata.saldoRenter[0].medlaantakerIdentifikatorListe
        assertEquals("123", medlaantakere[0])
        assertEquals("456", medlaantakere[1])
        assertEquals("789", medlaantakere[2])
        assertEquals("10", medlaantakere[3])
    }

    @Test
    fun saldorenteTestMedSkattepliktigGevinstMedBeloep() {
        val person = person {
            saldoRente {
                inntektsår = sistMuligAar
                innskudd {
                    beløp = 400_000
                }
                skattepliktigGevinst {
                    beløp = 5_000
                }
            }
        }

        val saldoRente = person.skattedata.saldoRenter[0]
        val gevinstListe = saldoRente.gevinstTapInnholdListe

        assertEquals(1, gevinstListe.size)
        assertEquals(InnholdType.SKATTEPLIKTIG_GEVINST, gevinstListe[0].innholdType)
        assertEquals(5_000L, gevinstListe[0].beloep)
        assertNull(saldoRente.valutakode)
    }

    @Test
    fun saldorenteTestMedSkattepliktigGevinstUtenBeloep() {
        val person = person {
            saldoRente {
                inntektsår = sistMuligAar
                innskudd {
                    beløp = 400_000
                }
                skattepliktigGevinst { }
            }
        }

        val saldoRente = person.skattedata.saldoRenter[0]
        val gevinstListe = saldoRente.gevinstTapInnholdListe

        assertEquals(1, gevinstListe.size)
        assertEquals(InnholdType.SKATTEPLIKTIG_GEVINST, gevinstListe[0].innholdType)
        assertNull(saldoRente.gevinstTapInnholdListe[0].beloep)
        assertNull(saldoRente.valutakode)
    }

    @Test
    fun saldorenteTestMedFradragsberettigetTapMedBeloep() {
        val person = person {
            saldoRente {
                inntektsår = sistMuligAar
                innskudd {
                    beløp = 400_000
                }
                fradragsberettigetTap {
                    beløp = 6_000
                }
                valutakode {
                    CHF
                }
            }
        }

        val saldoRente = person.skattedata.saldoRenter[0]
        val gevinstListe = saldoRente.gevinstTapInnholdListe

        assertEquals(1, gevinstListe.size)
        assertEquals(InnholdType.FRADEAGSBERETTIGET_TAP, gevinstListe[0].innholdType)
        assertEquals(6_000L, gevinstListe[0].beloep)
        assertEquals(Valutakode.CHF, saldoRente.valutakode)
    }

    @Test
    fun saldorenteTestMedFradragsberettigetTapUtenBeloepOgValutaKode() {
        val person = person {
            saldoRente {
                inntektsår = sistMuligAar
                innskudd {
                    beløp = 400_000
                }
                fradragsberettigetTap { }
                valutakode {
                    CHF
                }
            }
        }

        val saldoRente = person.skattedata.saldoRenter[0]
        val gevinstListe = saldoRente.gevinstTapInnholdListe

        assertEquals(1, gevinstListe.size)
        assertEquals(InnholdType.FRADEAGSBERETTIGET_TAP, gevinstListe[0].innholdType)
        assertNull(gevinstListe[0].beloep)
        assertEquals(Valutakode.CHF, saldoRente.valutakode)
    }

    @Test
    fun saldorenteTestMedValutakodeUtenKode() {
        val person = person {
            saldoRente {
                inntektsår = sistMuligAar
                innskudd {
                    beløp = 400_000
                }
                fradragsberettigetTap { }
                valutakode { }
            }
        }

        val saldoRente = person.skattedata.saldoRenter[0]
        val gevinstListe = saldoRente.gevinstTapInnholdListe

        assertEquals(1, gevinstListe.size)
        assertEquals(InnholdType.FRADEAGSBERETTIGET_TAP, gevinstListe[0].innholdType)
        assertNull(gevinstListe[0].beloep)
        assertEquals(Valutakode.USD, saldoRente.valutakode)
    }

    @Test
    fun saldorenteValiderMedFradragsberettigetTapOgSkattepliktigGevinst() {
        val exception = assertThrows(TdssDomeneException::class.java) {
            person {
                saldoRente {
                    inntektsår = sistMuligAar
                    innskudd {
                        beløp = 400_000
                    }
                    skattepliktigGevinst { }
                    fradragsberettigetTap { }
                }
            }
        }

        assertEquals(
            "saldoRente: fradragsberettigetTap og skattepliktigGevinst kan ikke kombineres for samme konto",
            exception.message
        )
    }

    @Test
    fun saldorenteValiderDuplikatSkattepliktigGevinst() {
        val exception = assertThrows(TdssDomeneException::class.java) {
            person {
                saldoRente {
                    inntektsår = sistMuligAar
                    innskudd {
                        beløp = 400_000
                    }
                    skattepliktigGevinst { }
                    skattepliktigGevinst { }
                }
            }
        }

        assertEquals("saldoRente: skattepliktigGevinst skal kun oppgis én gang", exception.message)
    }

    @Test
    fun saldorenteValiderDuplikatFradragsberettigetTap() {
        val exception = assertThrows(TdssDomeneException::class.java) {
            person {
                saldoRente {
                    inntektsår = sistMuligAar
                    innskudd {
                        beløp = 400_000
                    }
                    fradragsberettigetTap { }
                    fradragsberettigetTap { }
                }
            }
        }

        assertEquals("saldoRente: fradragsberettigetTap skal kun oppgis én gang", exception.message)
    }

    @Test
    fun saldorenteValiderDuplikatValutakode() {
        val exception = assertThrows(TdssDomeneException::class.java) {
            person {
                saldoRente {
                    inntektsår = sistMuligAar
                    innskudd {
                        beløp = 400_000
                    }
                    valutakode { JPY }
                    valutakode { SEK }
                }
            }
        }

        assertEquals("saldoRente: valutakode skal kun oppgis én gang", exception.message)
    }

    @Test
    fun saldorenteMedOpplysningspliktigOrganisasjonsnummerTest() {
        val person = person {
            saldoRente {
                inntektsår = sistMuligAar
                innskudd {
                    beløp = 300_000
                }
                opplysningspliktigOrganisasjonsnummer = "123456789"
            }
        }

        val saldoRente = person.skattedata.saldoRenter[0]
        assertEquals(sistMuligAar, saldoRente.inntektsaar)
        assertEquals("123456789", saldoRente.opplysningspliktigOrganisasjonsnummer.get())
    }

    @Test
    fun saldorenteMedFeilOpplysningspliktigOrganisasjonsnummerFormatTest() {
        val exception = assertThrows< TdssDomeneException> {
            person {
                saldoRente {
                    inntektsår = sistMuligAar
                    innskudd {
                        beløp = 300_000
                    }
                    opplysningspliktigOrganisasjonsnummer = "12345"
                }
            }
        }

        assert(exception.message!!.contains("selskapsmelding: spesifisert organisasjonsnummer '12345' må være et 9-sifret nummer"))
    }
}