package no.skatteetaten.rst.domene.grunnlagsdata.saldorente;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.jetbrains.annotations.Nullable;

import no.skatteetaten.rst.domene.grunnlagsdata.Valutakode;

public class SaldoRente implements Serializable {
    @Serial
    private static final long serialVersionUID = -5066789854728346982L;

    private int inntektsaar;
    private List<InnskuddUtlaanRente> innskuddUtlaanRenteListe;
    private List<Innhold> gevinstTapInnholdListe;
    private SaldoRenteKontotype saldoRenteKontotype;
    private Boolean avsluttetKonto;
    private SaldoRenteSkattekontoEgnethet saldoRenteSkattekontoEgnethet;
    private String disponentFoedselsnummer;
    private String disponentFornavn;
    private String disponentEtternavn;
    private List<String> medlaantakerIdentifikatorListe;
    private Valutakode valutakode;

    @Nullable
    private String opplysningspliktigOrganisasjonsnummer;

    public SaldoRente() {
    }

    public SaldoRente(
        int inntektsaar,
        List<InnskuddUtlaanRente> innskuddUtlaanRenteListe,
        SaldoRenteKontotype saldoRenteKontotype,
        Boolean avsluttetKonto,
        SaldoRenteSkattekontoEgnethet saldoRenteSkattekontoEgnethet,
        String disponentFoedselsnummer,
        @Nullable String opplysningspliktigOrganisasjonsnummer
    ) {
        this.inntektsaar = inntektsaar;
        this.innskuddUtlaanRenteListe = innskuddUtlaanRenteListe;
        this.saldoRenteKontotype = saldoRenteKontotype;
        this.avsluttetKonto = avsluttetKonto;
        this.saldoRenteSkattekontoEgnethet = saldoRenteSkattekontoEgnethet;
        this.disponentFoedselsnummer = disponentFoedselsnummer;
        this.opplysningspliktigOrganisasjonsnummer = opplysningspliktigOrganisasjonsnummer;
    }

    public int getInntektsaar() {
        return inntektsaar;
    }

    public void setInntektsaar(int inntektsaar) {
        this.inntektsaar = inntektsaar;
    }

    public List<InnskuddUtlaanRente> getInnskuddUtlaanRenteListe() {
        if (innskuddUtlaanRenteListe == null) {
            innskuddUtlaanRenteListe = new ArrayList<>();
        }
        return innskuddUtlaanRenteListe;
    }

    public SaldoRenteKontotype getSaldoRenteKontotype() {
        return saldoRenteKontotype;
    }

    public void setSaldoRenteKontotype(SaldoRenteKontotype saldoRenteKontotype) {
        this.saldoRenteKontotype = saldoRenteKontotype;
    }

    public Boolean isAvsluttetKonto() {
        return avsluttetKonto;
    }

    public void setAvsluttetKonto(Boolean avsluttetKonto) {
        this.avsluttetKonto = avsluttetKonto;
    }

    public SaldoRenteSkattekontoEgnethet getSaldoRenteSkattekontoEgnethet() {
        return saldoRenteSkattekontoEgnethet;
    }

    public void setSaldoRenteSkattekontoEgnethet(SaldoRenteSkattekontoEgnethet saldoRenteSkattekontoEgnethet) {
        this.saldoRenteSkattekontoEgnethet = saldoRenteSkattekontoEgnethet;
    }

    public String getDisponentFoedselsnummer() {
        return disponentFoedselsnummer;
    }

    public void setDisponentFoedselsnummer(String disponentFoedselsnummer) {
        this.disponentFoedselsnummer = disponentFoedselsnummer;
    }

    public void setDisponentFoedselsnummerIfNull(String foedselsnummer) {
        if (this.disponentFoedselsnummer == null) {
            this.disponentFoedselsnummer = foedselsnummer;
        }
    }

    public String getDisponentFornavn() {
        return disponentFornavn;
    }

    public void setDisponentFornavn(String disponentFornavn) {
        this.disponentFornavn = disponentFornavn;
    }

    public String getDisponentEtternavn() {
        return disponentEtternavn;
    }

    public void setDisponentEtternavn(String disponentEtternavn) {
        this.disponentEtternavn = disponentEtternavn;
    }

    public List<String> getMedlaantakerIdentifikatorListe() {
        if (medlaantakerIdentifikatorListe == null) {
            medlaantakerIdentifikatorListe = new ArrayList<>();
        }
        return medlaantakerIdentifikatorListe;
    }

    public List<Innhold> getGevinstTapInnholdListe() {
        if (gevinstTapInnholdListe == null) {
            gevinstTapInnholdListe = new ArrayList<>();
        }
        return gevinstTapInnholdListe;
    }

    public Valutakode getValutakode() {
        return valutakode;
    }

    public void setValutakode(Valutakode valutakode) {
        this.valutakode = valutakode;
    }

    public Optional<String> getOpplysningspliktigOrganisasjonsnummer() {
        return Optional.ofNullable(opplysningspliktigOrganisasjonsnummer);
    }

    public void setOpplysningspliktigOrganisasjonsnummer(@Nullable String opplysningspliktigOrganisasjonsnummer) {
        this.opplysningspliktigOrganisasjonsnummer = opplysningspliktigOrganisasjonsnummer;
    }
}
