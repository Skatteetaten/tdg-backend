package no.skatteetaten.rst.domene;

import java.io.Serial;
import java.io.Serializable;

public class Part implements Serializable {
    @Serial
    private static final long serialVersionUID = -3970437253771156273L;

    private Skattedata skattedata = new Skattedata();
    private String identifikatornummer;

    public Part() {
    }

    public Part(String identifikatornummer, Skattedata skattedata) {
        this.identifikatornummer = identifikatornummer;
        this.skattedata = skattedata;
    }

    public boolean erPerson() {
        return this instanceof Person;
    }

    public boolean erEnhet() {
        return this instanceof Enhet;
    }

    public String getIdentifikatornummer() {
        return identifikatornummer;
    }

    public void setIdentifikatornummer(String identifikatornummer) {
        this.identifikatornummer = identifikatornummer;
    }

    public Skattedata getSkattedata() {
        return skattedata;
    }

    public void setSkattedata(Skattedata skattedata) {
        this.skattedata = skattedata;
    }
}
