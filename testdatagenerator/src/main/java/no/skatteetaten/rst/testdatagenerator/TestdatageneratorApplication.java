package no.skatteetaten.rst.testdatagenerator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@SpringBootApplication
@OpenAPIDefinition(info = @Info(title = "Testdatagenerator API", description = "Verktøy for å generere dokumenter for en person eller en enhet"))
public class TestdatageneratorApplication {

    protected TestdatageneratorApplication() { }

    public static void main(String[] args) {
        SpringApplication.run(TestdatageneratorApplication.class, args);
    }

}
