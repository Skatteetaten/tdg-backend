package no.skatteetaten.rst.testdatagenerator.dokument;

import static no.skatteetaten.rst.testdatagenerator.util.Konstanter.ORG_NR_LENGDE;
import static no.skatteetaten.rst.testdatagenerator.util.KontonummerUtil.lagTilfeldigKontonummer;

import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import no.skatteetaten.rst.domene.grunnlagsdata.saldorente.Innhold;
import no.skatteetaten.rst.domene.grunnlagsdata.saldorente.InnskuddUtlaanRente;
import no.skatteetaten.rst.domene.grunnlagsdata.saldorente.SaldoRente;
import no.skatteetaten.rst.testdatagenerator.dto.MeldingDto;
import no.skatteetaten.rst.testdatagenerator.exception.TdssParseException;
import no.skatteetaten.saldorente.Disponent;
import no.skatteetaten.saldorente.GevinstEllerTap;
import no.skatteetaten.saldorente.Kontotype;
import no.skatteetaten.saldorente.Leveranse;
import no.skatteetaten.saldorente.Leveransetype;
import no.skatteetaten.saldorente.Medlaantaker;
import no.skatteetaten.saldorente.Melding;
import no.skatteetaten.saldorente.ObjectFactory;
import no.skatteetaten.saldorente.OppgaveSaldoRente;
import no.skatteetaten.saldorente.Oppgaveeier;
import no.skatteetaten.saldorente.Oppgavegiver;
import no.skatteetaten.saldorente.OppgaveoppsummeringSaldoRente;
import no.skatteetaten.saldorente.Renter;

/**
 * Utfyller alle deler av et SaldoRente-dokument basert på innsendt domenemodell.
 * <p>
 * Klassen håndterer mapping fra den interne TDSS-modellen ({@link SaldoRente})
 * til de JAXB-genererte XML-modellene som brukes for marshalling til XML-dokumenter.
 */
@Component
public class SaldoRenteDokumentUtfyller {
    private static final ObjectFactory FACTORY = new ObjectFactory();

    /**
     * Genererer en komplett SaldoRente-melding basert på verdiene som er angitt i {@link SaldoRente},
     * og fyller automatisk inn alle påkrevde felter som ikke er spesifisert.
     *
     * @param saldoRente   Domenemodellen for {@link SaldoRente} som inneholder brukerens spesifikasjoner.
     * @param oppgaveeier  Oppgaveeier (person eller enhet) som skal knyttes til oppgaven.
     * @param oppgavegiver Oppgavegiver (enhet) som står for innsending av oppgaven.
     * @return En komplett {@link MeldingDto} klar for marshalling til XML.
     */
    public MeldingDto lagMelding(SaldoRente saldoRente, Oppgaveeier oppgaveeier, Oppgavegiver oppgavegiver) {
        OppgaveSaldoRente oppgaveSaldoRente = FACTORY.createOppgaveSaldoRente();
        OppgaveoppsummeringSaldoRente oppgaveoppsummeringSaldoRente = FACTORY.createOppgaveoppsummeringSaldoRente();
        oppgaveoppsummeringSaldoRente.setAntallOppgaver(1);

        String leveranseReferanse = "TDG-" + UUID.randomUUID();

        fyllUtInnskuddUtlaanRenter(saldoRente, oppgaveSaldoRente, oppgaveoppsummeringSaldoRente);
        fyllUtKonto(saldoRente, oppgaveSaldoRente);
        fyllUtDisponent(saldoRente, oppgaveSaldoRente);
        fyllUtGevinstEllerTap(saldoRente, oppgaveSaldoRente, oppgaveoppsummeringSaldoRente);

        return new MeldingDto(leveranseReferanse, new Melding()
            .withLeveranse(new Leveranse()
                .withOppgave(oppgaveSaldoRente
                    .withOppgaveeier(oppgaveeier)
                )
                .withOppgavegiversLeveranseReferanse(leveranseReferanse)
                .withLeveransetype(Leveransetype.ORDINAER)
                .withInntektsaar(String.valueOf(saldoRente.getInntektsaar()))
                .withKildesystem("TDG-Kildesystem")
                .withOppgavegiver(oppgavegiver)
                .withOppgaveoppsummering(oppgaveoppsummeringSaldoRente)
            ));
    }

    private static void fyllUtInnskuddUtlaanRenter(
        SaldoRente saldoRente,
        OppgaveSaldoRente oppgaveSaldoRente,
        OppgaveoppsummeringSaldoRente oppgaveoppsummeringSaldoRente
    ) {
        Renter renter = FACTORY.createRenter();

        for (InnskuddUtlaanRente innskuddUtlaanRente : saldoRente.getInnskuddUtlaanRenteListe()) {
            long beloep = innskuddUtlaanRente.getBeloep().longValue();
            // Det er kun lovlig med ett innskudd av samme type per saldo rente dokument. Derfor settes sum lik beløp.
            switch (innskuddUtlaanRente.getInnskuddUtlaanRenteType()) {
                case INNSKUDD -> {
                    oppgaveSaldoRente.setInnskudd(beloep);
                    oppgaveoppsummeringSaldoRente.setSumInnskudd(beloep);
                }
                case UTLAAN -> {
                    oppgaveSaldoRente.setUtlaan(beloep);
                    oppgaveoppsummeringSaldoRente.setSumUtlaan(beloep);
                    for (String medlaantakerId : saldoRente.getMedlaantakerIdentifikatorListe()) {
                        Medlaantaker medlaantaker = new Medlaantaker();
                        if (medlaantakerId.length() == ORG_NR_LENGDE) {
                            medlaantaker.setOrganisasjonsnummer(medlaantakerId);
                        } else {
                            medlaantaker.setFoedselsnummer(medlaantakerId);
                        }
                        oppgaveSaldoRente.getMedlaantaker().add(medlaantaker);
                    }
                }
                case OPPTJENTE_RENTER -> {
                    renter.setOpptjenteRenter(beloep);
                    oppgaveoppsummeringSaldoRente.setSumOpptjenteRenter(beloep);
                }
                case PAALOEPTE_RENTER -> {
                    renter.setPaaloepteRenter(beloep);
                    oppgaveoppsummeringSaldoRente.setSumPaaloepteRenter(beloep);
                }
                case BETALTE_MISLIGHOLDTE_RENTER -> {
                    renter.setBetalteMisligholdteRenter(beloep);
                    oppgaveoppsummeringSaldoRente
                        .setSumBetalteMisligholdteRenter(beloep);
                }
                case TILBAKEBETALTE_MISLIGHOLDTE_RENTER -> {
                    renter.setTilbakebetalteMisligholdteRenter(beloep);
                    oppgaveoppsummeringSaldoRente.setSumTilbakebetalteMisligholdteRenter(beloep);
                }
                default -> throw new TdssParseException("Ugyldig innskudd-, utlån- eller rentetype");
            }
        }

        oppgaveSaldoRente.setRenter(renter);
    }

    private static void fyllUtGevinstEllerTap(
        SaldoRente saldoRente,
        OppgaveSaldoRente oppgaveSaldoRente,
        OppgaveoppsummeringSaldoRente oppgaveoppsummeringSaldoRente
    ) {
        if (saldoRente.getGevinstTapInnholdListe().isEmpty()) {
            return;
        }

        GevinstEllerTap gevinstEllerTap = FACTORY.createGevinstEllerTap();
        Innhold innhold = saldoRente.getGevinstTapInnholdListe().getFirst();
        long beloep = innhold.getBeloep();
        switch (innhold.getInnholdType()) {
            case SKATTEPLIKTIG_GEVINST -> {
                gevinstEllerTap.setSkattepliktigGevinst(beloep);
                oppgaveoppsummeringSaldoRente.setSumSkattepliktigGevinst(beloep);
            }
            case FRADEAGSBERETTIGET_TAP ->  {
                gevinstEllerTap.setFradragsberettigetTap(beloep);
                oppgaveoppsummeringSaldoRente.setSumFradragsberettigetTap(beloep);
            }
            default -> throw new TdssParseException("Ugyldig gevinst- eller tap-type");
        }

        oppgaveSaldoRente.setGevinstEllerTap(gevinstEllerTap);
    }

    private static void fyllUtDisponent(SaldoRente saldoRente, OppgaveSaldoRente oppgaveSaldoRente) {
        if (StringUtils.isNotEmpty(saldoRente.getDisponentFoedselsnummer())
            && StringUtils.isNotEmpty(saldoRente.getDisponentFornavn())
            && StringUtils.isNotEmpty(saldoRente.getDisponentEtternavn())
        ) {
            Disponent disponent = FACTORY.createDisponent();
            disponent.setFoedselsnummer(saldoRente.getDisponentFoedselsnummer());
            disponent.setFornavn(saldoRente.getDisponentFornavn());
            disponent.setEtternavn(saldoRente.getDisponentEtternavn());
            oppgaveSaldoRente.getDisponent().add(disponent);
        }
    }

    private static void fyllUtKonto(SaldoRente saldoRente, OppgaveSaldoRente oppgaveSaldoRente) {
        oppgaveSaldoRente.setKontonummer(lagTilfeldigKontonummer());
        oppgaveSaldoRente.setAvsluttetKonto(saldoRente.isAvsluttetKonto());
        if (null != saldoRente.getSaldoRenteKontotype()) {
            oppgaveSaldoRente.setKontotype(Kontotype.fromValue(saldoRente.getSaldoRenteKontotype().getVerdi()));
        }
        if (null != saldoRente.getSaldoRenteSkattekontoEgnethet()) {
            oppgaveSaldoRente.setSkattekontoEgnethet(saldoRente.getSaldoRenteSkattekontoEgnethet().getVerdi());
        }
        if (null != saldoRente.getValutakode()) {
            oppgaveSaldoRente.setValutakode(saldoRente.getValutakode().name());
        }
    }
}
