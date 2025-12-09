package no.skatteetaten.rst.testdatagenerator.parser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import no.skatteetaten.rst.domene.Part;
import no.skatteetaten.rst.testdatagenerator.exception.TdssParseException;

/**
 * Parser TDSS-spesifikasjoner og konverterer dem til {@link Part} objekter.
 * <p>
 * Bruker Kotlin Script Engine til å evaluere TDSS-kode. Det kreves at spesifikasjonen beskriver akkurat en person eller en enhet.
 */
@Component
@RequiredArgsConstructor
public class TdssParser {

    private static final String OPPRETT_PART_TEMPLATE =
        """
            import no.skatteetaten.rst.tdss.person.person
            import no.skatteetaten.rst.tdss.enhet.enhet
            fun opprettPart() = %s""";

    /**
     * Oppretter en {@link Part} basert på en TDSS-spesifikasjon.
     * <p>
     * Metoden validerer at spesifikasjonen inneholder kun en person eller enhet,
     * pakker den inn i et Kotlin-skript, og evaluerer dette via Kotlin Script Engine.
     *
     * @param tdss TDSS-spesifikasjonen som streng.
     * @return En {@link Part} konstruert fra spesifikasjonen.
     * @throws TdssParseException dersom spesifikasjonen er ugyldig eller ikke kan evalueres.
     */
    public Part opprettPartFraTdss(String tdss) {
        if (tellAntallSpesifiserteParter(tdss) > 1) {
            throw new TdssParseException("Kun en person eller enhet kan spesifiseres om gangen");
        }

        String kode = String.format(OPPRETT_PART_TEMPLATE, tdss);
        ScriptEngine engine = new ScriptEngineManager().getEngineByExtension("kts");
        try {
            engine.eval(kode);
            Invocable invocator = (Invocable) engine;
            return (Part) invocator.invokeFunction("opprettPart");
        } catch (ScriptException | NoSuchMethodException e) {
            throw new TdssParseException("Feil under evaluering av spesifikasjon: " + e.getMessage(), e);
        }
    }

    private int tellAntallSpesifiserteParter(String tdss) {
        String personEllerEnhetRegex = "\\b(person|enhet)\\b\\s*\\{";
        Pattern pattern = Pattern.compile(personEllerEnhetRegex);
        Matcher matcher = pattern.matcher(tdss);
        return (int) matcher.results().count();
    }
}
