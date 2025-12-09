package no.skatteetaten.rst.domene.grunnlagsdata.saldorente;

import java.io.Serial;
import java.io.Serializable;

public class Innhold implements Serializable {
    @Serial
    private static final long serialVersionUID = -4409472988540261533L;

    private Long beloep;
    private InnholdType innholdType;

    public Innhold() {
    }

    public Innhold(Long beloep, InnholdType innholdType) {
        this.beloep = beloep;
        this.innholdType = innholdType;
    }

    public Innhold(InnholdType innholdType) {
        this.innholdType = innholdType;
    }

    public Long getBeloep() {
        return beloep;
    }

    public void setBeloep(Long beloep) {
        this.beloep = beloep;
    }

    public InnholdType getInnholdType() {
        return innholdType;
    }

    public void setInnholdType(InnholdType innholdType) {
        this.innholdType = innholdType;
    }
}
