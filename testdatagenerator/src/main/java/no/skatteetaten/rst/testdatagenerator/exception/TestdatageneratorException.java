package no.skatteetaten.rst.testdatagenerator.exception;

public class TestdatageneratorException extends RuntimeException {
    public TestdatageneratorException(String feilmelding) {
        super(feilmelding);
    }

    public TestdatageneratorException(String feilmelding, Throwable cause) {
        super(feilmelding, cause);
    }

    @Override
    public String toString() {
        return getLocalizedMessage();
    }
}
