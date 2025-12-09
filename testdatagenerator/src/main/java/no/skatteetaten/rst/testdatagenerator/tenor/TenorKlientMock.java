package no.skatteetaten.rst.testdatagenerator.tenor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Denne ligger i src/main for enkelhetens skyld, og fungerer som en erstatning for {@link TenorKlientMaskinport} så lenge den ikke er implementert.
 */
@Component
@Profile("dev")
public class TenorKlientMock implements TenorKlient {
    private static final Random RANDOM = new Random();

    private static final Map<String, String> PERSONER = Map.of(
        "26890296253", "Bra Gitar",
        "21929897614", "Gratis Krabbe",
        "30897898051", "Kantete Pause",
        "23838197175", "Kunstig Ekspedisjon",
        "28885497693", "Sitrongul Lenestol Pedagog",
        "10919396674", "Altetende Logaritme",
        "21884995504", "Trekantet Gyngehest",
        "07926399971", "Ekstra Gjesterom",
        "12907599866", "Rik Industri",
        "28887199193", "Kort Filosof"
    );
    private static final Map<String, String> ENHETER = Map.of(
        "315403618", "KVART MATEMATISK FJELLREV",
        "313596443", "STRIDLYNT SØVNIG TIGER AS",
        "314322045", "NY UPRAKTISK KATT KLAPPSTOL",
        "315408938", "BLOMSTRETE LILLA TIGER AS",
        "310527335", "HYGGELIG MOSEGRODD APE",
        "310521051", "LYDIG SKEPTISK TIGER AS",
        "213589202", "OPERATIV FORSIKTIG KATT DOMPAP",
        "311775626", "SPRUDLENDE VARM ISBJØRN SA",
        "312306417", "PRAKTISK OVERMODIG FJELLREV",
        "310643106", "MORSOM VOKAL TIGER AS"
    );

    @Override
    public Optional<TenorPart> hentPerson(String foedselsnummer) {
        return Optional.ofNullable(PERSONER.get(foedselsnummer))
            .map(navn -> new TenorPart(foedselsnummer, navn));
    }

    @Override
    public Optional<TenorPart> hentEnhet(String organisasjonsnummer) {
        return Optional.ofNullable(ENHETER.get(organisasjonsnummer))
            .map(navn -> new TenorPart(organisasjonsnummer, navn));
    }

    @Override
    public TenorPart hentTilfeldigPart(PartType partType) {
        Map<String, String> map = switch (partType) {
            case PartType.PERSON -> PERSONER;
            case PartType.ENHET -> ENHETER;
        };

        List<String> keys = new ArrayList<>(map.keySet());
        String key = keys.get(RANDOM.nextInt(keys.size()));

        return new TenorPart(key, map.get(key));
    }
}
