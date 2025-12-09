package no.skatteetaten.rst.domene.grunnlagsdata.saldorente;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigInteger;

public class InnskuddUtlaanRente implements Serializable {
    @Serial
    private static final long serialVersionUID = 3388041839436377645L;

    private BigInteger beloep;
    private InnskuddUtlaanRenteType innskuddUtlaanRenteType;

    public InnskuddUtlaanRente() {
    }

    public InnskuddUtlaanRente(BigInteger beloep, InnskuddUtlaanRenteType innskuddUtlaanRenteType) {
        this.beloep = beloep;
        this.innskuddUtlaanRenteType = innskuddUtlaanRenteType;
    }

    public BigInteger getBeloep() {
        return beloep;
    }

    public void setBeloep(BigInteger beloep) {
        this.beloep = beloep;
    }

    public InnskuddUtlaanRenteType getInnskuddUtlaanRenteType() {
        return innskuddUtlaanRenteType;
    }

    public void setInnskuddUtlaanRenteType(InnskuddUtlaanRenteType innskuddUtlaanRenteType) {
        this.innskuddUtlaanRenteType = innskuddUtlaanRenteType;
    }
}
