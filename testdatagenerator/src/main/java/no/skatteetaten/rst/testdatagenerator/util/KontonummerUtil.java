package no.skatteetaten.rst.testdatagenerator.util;

import org.apache.commons.lang3.StringUtils;

public final class KontonummerUtil {
    private static final int KONTONUMMER_999_999_999 = 999_999_999;
    private static final int KONTONUMMER_LENGDE = 11;
    private static final int ANTALL_SIFFER_FOER_KONTROLLSIFFER = 10;

    private KontonummerUtil() {
    }

    public static String lagTilfeldigKontonummer() {
        String kontonummer = "";
        while (kontonummer.length() != KONTONUMMER_LENGDE) {
            try {
                kontonummer = String.format("%09d", RandomUtil.lagTilfeldigTallMellom(0, KONTONUMMER_999_999_999))
                    + String.format("%01d", RandomUtil.lagTilfeldigTallMellom(0, 9));
                kontonummer = kontonummer + lagKontrollsiffer(kontonummer);
            } catch (IllegalArgumentException iae) {
                kontonummer = "";
            }
        }
        return kontonummer;
    }

    /**
     * Koden under er en modifisert utgave av java-koden for å sjekke kontonummer, funnet på wikipedia.
     * Link: <a href="https://no.wikipedia.org/wiki/MOD11">https://no.wikipedia.org/wiki/MOD11</a>
     *
     * @param tiSifretKontoNr Ideelt sett, 10 tilfeldige tall. For helt korrekt bruk så bør bankfilial også
     *                        eksistere, dvs. 4 første siffer bør peke til en eksisterende bank.
     * @return Sjekknummeret, som er siste siffer i et kontonummer.
     * @throws IllegalArgumentException Dersom sjekknummeret er 10, noe som skjer, så skal kontonummeret avvises per definisjon.
     */
    private static int lagKontrollsiffer(String tiSifretKontoNr) throws IllegalArgumentException {
        if (tiSifretKontoNr.length() != ANTALL_SIFFER_FOER_KONTROLLSIFFER || !StringUtils.isNumeric(tiSifretKontoNr)) {
            throw new IllegalArgumentException("Trenger " + ANTALL_SIFFER_FOER_KONTROLLSIFFER + " første siffer i tenkt kontonummer");
        }
        int sum = 0;
        for (int i = 0; i < ANTALL_SIFFER_FOER_KONTROLLSIFFER; i++) {
            int tall = Character.getNumericValue(tiSifretKontoNr.charAt(i));
            int vektnr = 7 - (i + 2) % 6;
            sum += tall * vektnr;
        }
        final int divisorIMOD11 = 11;
        int rest = sum % divisorIMOD11;
        if (1 == rest) { // Kontrollsiffer 11 - 1 = 10
            throw new IllegalArgumentException(String.format(
                "Kontonummer '%s' genererer sjekksiffer 10 som tilsier at kontonummeret skal avvises per algoritmen.",
                tiSifretKontoNr));
        }
        return (rest == 0) ? 0 : divisorIMOD11 - rest;
    }
}
