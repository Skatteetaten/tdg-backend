package no.skatteetaten.rst.domene.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class TdssDomeneException extends RuntimeException {

    public TdssDomeneException(String feilmelding) {
        super(feilmelding);
    }

    public TdssDomeneException(String feilmelding, Throwable cause) {
        super(feilmelding, cause);
    }

    @Override
    public String toString() {
        return getLocalizedMessage();
    }
}
