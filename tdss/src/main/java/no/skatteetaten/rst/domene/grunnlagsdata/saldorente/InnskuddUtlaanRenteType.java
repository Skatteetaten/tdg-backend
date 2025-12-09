package no.skatteetaten.rst.domene.grunnlagsdata.saldorente;

import no.skatteetaten.FromVerdiEnum;

public enum InnskuddUtlaanRenteType implements FromVerdiEnum {
    INNSKUDD("innskudd"),
    UTLAAN("utlaan"),
    OPPTJENTE_RENTER("opptjenteRenter"),
    PAALOEPTE_RENTER("paaloepteRenter"),
    BETALTE_MISLIGHOLDTE_RENTER("betalteMisligholdteRenter"),
    TILBAKEBETALTE_MISLIGHOLDTE_RENTER("tilbakebetalteMisligholdteRenter");

    private final String verdi;

    InnskuddUtlaanRenteType(String verdi) {
        this.verdi = verdi;
    }

    @Override
    public String getVerdi() {
        return verdi;
    }
}
