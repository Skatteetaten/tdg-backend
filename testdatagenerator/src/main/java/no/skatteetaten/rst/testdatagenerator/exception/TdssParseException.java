package no.skatteetaten.rst.testdatagenerator.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class TdssParseException extends RuntimeException {
    public TdssParseException(String feilmelding) {
        super(feilmelding);
    }

    public TdssParseException(String feilmelding, Throwable cause) {
        super(feilmelding, cause);
    }

    @Override
    public String toString() {
        return getLocalizedMessage();
    }
}
