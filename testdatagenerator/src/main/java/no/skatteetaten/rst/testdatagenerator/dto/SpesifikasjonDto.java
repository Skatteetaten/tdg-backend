package no.skatteetaten.rst.testdatagenerator.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(example =
    """
        {
            "spesifikasjon": "person { fødselsnummer = \\\"26890296253\\\" ; saldoRente { inntektsår = 2025 ; innskudd { beløp = 1000 } } }"
        }
        """, description = "TDSS for eksisterende person med saldoRente-melding"
)
public class SpesifikasjonDto {
    private String spesifikasjon;
}
