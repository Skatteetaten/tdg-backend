package no.skatteetaten.rst.testdatagenerator.util;

import java.util.concurrent.ThreadLocalRandom;

public final class RandomUtil {

    private RandomUtil() {
    }

    public static int lagTilfeldigTallMellom(int fraInkludert, int tilInkludert) {
        if (fraInkludert < 0) {
            throw new IllegalArgumentException("Fra-verdi må være 0 eller større");
        } else if (fraInkludert >= tilInkludert) {
            throw new IllegalArgumentException("Til-verdi må være større enn fra-verdi.");
        }

        return ThreadLocalRandom.current().nextInt(fraInkludert, tilInkludert + 1);
    }

    public static long lagTilfeldigTallMellom(long fraInkludert, long tilInkludert) {
        if (fraInkludert < 0) {
            throw new IllegalArgumentException("Fra-verdi må være 0 eller større");
        } else if (fraInkludert >= tilInkludert) {
            throw new IllegalArgumentException("Til-verdi må være større enn fra-verdi.");
        }

        return ThreadLocalRandom.current().nextLong(fraInkludert, tilInkludert + 1);
    }
}
