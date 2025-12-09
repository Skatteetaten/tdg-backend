package no.skatteetaten.rst.domene;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import no.skatteetaten.rst.domene.grunnlagsdata.saldorente.SaldoRente;

public class Skattedata implements Serializable {
    @Serial
    private static final long serialVersionUID = 5052038052291192522L;

    private List<SaldoRente> saldoRenter;

    public List<SaldoRente> getSaldoRenter() {
        if (saldoRenter == null) {
            saldoRenter = new ArrayList<>();
        }
        return saldoRenter;
    }

    public void setSaldoRenter(List<SaldoRente> saldoRenter) {
        this.saldoRenter = saldoRenter;
    }
}
