package no.skatteetaten.rst.domene.grunnlagsdata.saldorente;

import no.skatteetaten.FromVerdiEnum;

public enum SaldoRenteKontotype implements FromVerdiEnum {
    INNSKUDDSKONTO_I_NOK("innskuddskontoINok"),
    INNSKUDDSKONTO_I_UTENLANDSK_VALUTA("innskuddskontoIUtenlandskValuta");

    private final String verdi;

    SaldoRenteKontotype(String verdi) {
        this.verdi = verdi;
    }

    @Override
    public String getVerdi() {
        return verdi;
    }
}
