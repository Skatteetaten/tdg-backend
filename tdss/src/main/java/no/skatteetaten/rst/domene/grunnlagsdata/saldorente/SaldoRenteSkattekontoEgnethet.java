package no.skatteetaten.rst.domene.grunnlagsdata.saldorente;

import no.skatteetaten.FromVerdiEnum;

public enum SaldoRenteSkattekontoEgnethet implements FromVerdiEnum {
    SALDO_RENTE_SKATTEKONTO_EGNETHET_0("0"),
    SALDO_RENTE_SKATTEKONTO_EGNETHET_1("1");

    private final String verdi;

    SaldoRenteSkattekontoEgnethet(String verdi) {
        this.verdi = verdi;
    }

    @Override
    public String getVerdi() {
        return verdi;
    }
}
