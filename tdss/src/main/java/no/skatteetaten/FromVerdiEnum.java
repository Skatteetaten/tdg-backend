package no.skatteetaten;

import java.util.Arrays;
import java.util.Objects;

public interface FromVerdiEnum {
    static <T extends Enum<T> & FromVerdiEnum> T fromVerdi(Class<T> enumClass, String verdi) {
        return Arrays.stream(enumClass.getEnumConstants())
            .filter(k -> Objects.equals(k.getVerdi(), verdi))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Det finnes ingen type med verdi " + verdi + " i enumen " + enumClass.getSimpleName()));
    }

    String getVerdi();
}
