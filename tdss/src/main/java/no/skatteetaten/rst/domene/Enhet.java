package no.skatteetaten.rst.domene;

import java.io.Serial;
import java.io.Serializable;

public class Enhet extends Part implements Serializable {
    @Serial
    private static final long serialVersionUID = 3246465188788546744L;

    public Enhet() {
        super();
    }

    public Enhet(String organisasjonsnummer, Skattedata skattedata) {
        super(organisasjonsnummer, skattedata);
    }

    public String getOrganisasjonsnummer() {
        return getIdentifikatornummer();
    }

    public void setOrganisasjonsnummer(String organisasjonsnummer) {
        setIdentifikatornummer(organisasjonsnummer);
    }
}
