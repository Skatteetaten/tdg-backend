package no.skatteetaten.rst.domene;

import java.io.Serial;
import java.io.Serializable;

public class Person extends Part implements Serializable {
    @Serial
    private static final long serialVersionUID = -7510158174810926051L;

    public Person() {
        super();
    }

    public Person(String foedselsnummer, Skattedata skattedata) {
        super(foedselsnummer, skattedata);
    }

    public String getFoedselsnummer() {
        return getIdentifikatornummer();
    }

    public void setFoedselsnummer(String foedselsnummer) {
        setIdentifikatornummer(foedselsnummer);
    }
}
