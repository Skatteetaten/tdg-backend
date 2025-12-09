package no.skatteetaten.rst.testdatagenerator.util;

import java.io.StringWriter;
import java.net.URL;

import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.xml.sax.SAXException;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import no.skatteetaten.rst.testdatagenerator.exception.TestdatageneratorException;
import no.skatteetaten.saldorente.Melding;

public final class XmlUtil {

    private XmlUtil() {
    }

    public static <T> String marshallOgValider(JAXBElement<T> jaxbelement, URL schemaUrl) {
        StringWriter writer = new StringWriter();

        try {
            JAXBContext context = JAXBContext.newInstance(Melding.class);
            Marshaller m = context.createMarshaller();
            Schema schema = SchemaFactory.newDefaultInstance().newSchema(schemaUrl);

            if (schema != null) {
                m.setSchema(schema);
            }

            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            m.marshal(jaxbelement, writer);

            return writer.toString();
        } catch (SAXException e) {
            throw new TestdatageneratorException("Kunne ikke opprette XSD schema for: " + schemaUrl.toString(), e);
        } catch (JAXBException e) {
            throw new TestdatageneratorException("Feil under marshalling av innsending", e);
        }
    }
}
