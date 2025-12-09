package no.skatteetaten.rst.testdatagenerator.dto;

import static no.skatteetaten.rst.testdatagenerator.util.Konstanter.SALDO_RENTE_XSD_RESOURCE;

import no.skatteetaten.rst.testdatagenerator.util.XmlUtil;
import no.skatteetaten.saldorente.Melding;
import no.skatteetaten.saldorente.ObjectFactory;

public record MeldingDto(String leveranseReferanse, Melding melding) {

    public DokumentDto tilDokumentDto(ObjectFactory factory) {
        return new DokumentDto(
            leveranseReferanse,
            XmlUtil.marshallOgValider(
                factory.createMelding(melding),
                getClass().getClassLoader().getResource(SALDO_RENTE_XSD_RESOURCE)
            )
        );
    }
}
