package no.skatteetaten.rst.testdatagenerator.dokument;

import java.math.BigInteger;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import lombok.RequiredArgsConstructor;
import no.skatteetaten.rst.domene.Part;
import no.skatteetaten.rst.domene.grunnlagsdata.Valutakode;
import no.skatteetaten.rst.domene.grunnlagsdata.saldorente.Innhold;
import no.skatteetaten.rst.domene.grunnlagsdata.saldorente.InnskuddUtlaanRente;
import no.skatteetaten.rst.domene.grunnlagsdata.saldorente.SaldoRente;
import no.skatteetaten.rst.testdatagenerator.exception.TdssParseException;
import no.skatteetaten.rst.testdatagenerator.tenor.PartType;
import no.skatteetaten.rst.testdatagenerator.tenor.TenorKlient;
import no.skatteetaten.rst.testdatagenerator.tenor.TenorPart;
import no.skatteetaten.rst.testdatagenerator.util.RandomUtil;
import no.skatteetaten.saldorente.Oppgaveeier;
import no.skatteetaten.saldorente.Oppgavegiver;

/**
 * Utfyller SaldoRente-domenemodellen med manglende verdier og kobler den mot data fra Tenor.
 * Klassen sørger for at alle obligatoriske felter er satt før dokumentgenerering.
 */
@Component
@RequiredArgsConstructor
public class SaldoRenteDomeneUtfyller {
    private final TenorKlient tenorKlient;

    /**
     * Fyller ut alle påkrevde felter i en {@link Part} før dokumentgenerering.
     *
     * @param part Domenemodell som inneholder SaldoRente-spesifikasjoner.
     * @return Oppgaveeier (person eller enhet) som skal knyttes til oppgaven.
     */
    public Oppgaveeier fyllUtPart(Part part) {
        if (part.getSkattedata().getSaldoRenter().isEmpty()) {
            throw new TdssParseException("Minst en saldoRente oppgave må spesifiseres.");
        }

        if (part.getIdentifikatornummer() == null) {
            PartType partType = part.erPerson() ? PartType.PERSON : PartType.ENHET;
            TenorPart tenorPart = tenorKlient.hentTilfeldigPart(partType);
            part.setIdentifikatornummer(tenorPart.identifikator());
        }

        for (SaldoRente saldoRente : part.getSkattedata().getSaldoRenter()) {
            List<Innhold> innholdListe = saldoRente.getGevinstTapInnholdListe();
            if (innholdListe.size() > 1) {
                throw new TdssParseException("saldoRente: kan enten ha én skattepliktigGevinst eller én fradragsberettigetTap.");
            }
            if (innholdListe.size() == 1 && innholdListe.getFirst().getBeloep() == null) {
                innholdListe.getFirst().setBeloep(RandomUtil.lagTilfeldigTallMellom(1_000L, 15_000L));
            }

            if (saldoRente.getValutakode() == Valutakode.IKKE_SPESIFISERT) {
                saldoRente.setValutakode(Valutakode.USD);
            }

            fyllMedBeloep(saldoRente.getInnskuddUtlaanRenteListe());
            fyllUtDisponent(saldoRente);
        }

        return part.erPerson()
            ? fyllUtOppgaveeierPerson(part)
            : fyllUtOppgaveeierEnhet(part);
    }

    /**
     Oppretter en {@link Oppgavegiver} basert på enten brukerens spesifikasjon
     * eller en tilfeldig enhet hentet fra Tenor.
     * <p>
     * Dersom brukeren har oppgitt et opplysningspliktigOrganisasjonsnummer, forsøkes dette slått opp i Tenor.
     * Hvis ikke så hentes en tilfeldig gyldig enhet fra Tenor.
     *
     * @param saldoRente Domenemodellen for {@link SaldoRente} som inneholder brukerens spesifikasjoner.
     * @return Oppgavegiver (enhet) som står for innsending av oppgaven.
     */
    public Oppgavegiver fyllUtOppgavegiver(SaldoRente saldoRente) {
        TenorPart tenorPart = saldoRente.getOpplysningspliktigOrganisasjonsnummer().isEmpty()
            ? tenorKlient.hentTilfeldigPart(PartType.ENHET)
            : hentTenorEnhet(saldoRente.getOpplysningspliktigOrganisasjonsnummer().get());

        return new Oppgavegiver()
            .withOrganisasjonsnummer(tenorPart.identifikator())
            .withOrganisasjonsnavn(tenorPart.navn());
    }


    private Oppgaveeier fyllUtOppgaveeierPerson(Part part) {
        TenorPart tenorPart = part.getIdentifikatornummer().isEmpty()
            ? tenorKlient.hentTilfeldigPart(PartType.PERSON)
            : hentTenorPerson(part.getIdentifikatornummer());

        List<String> navn = List.of(tenorPart.navn().split(" "));

        return new Oppgaveeier()
            .withFornavn(navn.getFirst())
            .withEtternavn(navn.getLast())
            .withFoedselsnummer(tenorPart.identifikator());
    }

    private Oppgaveeier fyllUtOppgaveeierEnhet(Part part) {
        TenorPart tenorPart = part.getIdentifikatornummer().isEmpty()
            ? hentTenorEnhet(part.getIdentifikatornummer())
            : tenorKlient.hentTilfeldigPart(PartType.ENHET);

        return new Oppgaveeier()
            .withOrganisasjonsnavn(tenorPart.navn())
            .withOrganisasjonsnummer(tenorPart.identifikator());
    }


    private TenorPart hentTenorPerson(String foedselsnummer) {
        return tenorKlient.hentPerson(foedselsnummer)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Det finnes ingen personer med fødselsnummer: " + foedselsnummer)
            );
    }

    private TenorPart hentTenorEnhet(String organisasjonsnummer) {
        return tenorKlient.hentEnhet(organisasjonsnummer)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Det finnes ingen enheter med organisasjonsnummer: " + organisasjonsnummer)
            );
    }

    private void fyllMedBeloep(List<InnskuddUtlaanRente> innskuddListe) {
        for (InnskuddUtlaanRente innskuddUtlaanRente : innskuddListe) {
            if (innskuddUtlaanRente.getBeloep() != null) {
                continue;
            }

            Pair<Long, Long> minMaxBeloep = switch (innskuddUtlaanRente.getInnskuddUtlaanRenteType()) {
                case INNSKUDD, UTLAAN -> Pair.of(1_000L, 1_000_000L);
                case OPPTJENTE_RENTER, PAALOEPTE_RENTER -> Pair.of(300L, 50_000L);
                case BETALTE_MISLIGHOLDTE_RENTER, TILBAKEBETALTE_MISLIGHOLDTE_RENTER -> Pair.of(300L, 20_000L);
            };

            innskuddUtlaanRente.setBeloep(BigInteger.valueOf(RandomUtil.lagTilfeldigTallMellom(minMaxBeloep.getLeft(), minMaxBeloep.getRight())));
        }
    }

    private void fyllUtDisponent(SaldoRente saldoRente) {
        if (StringUtils.isNotEmpty(saldoRente.getDisponentFoedselsnummer())) {
            TenorPart tenorPart = hentTenorPerson(saldoRente.getDisponentFoedselsnummer());
            List<String> navn = List.of(tenorPart.navn().split(" "));
            saldoRente.setDisponentFornavn(navn.getFirst());
            saldoRente.setDisponentEtternavn(navn.getLast());
            saldoRente.setDisponentFoedselsnummer(tenorPart.identifikator());
        }
    }
}
