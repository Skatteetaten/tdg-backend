# Testdatageneratoren Backend

## Om Testdatageneratoren - overordnet beskrivelse av løsningen

*Testdatageneratoren* er en webapplikasjon som kan brukes for å generere syntetiske testdata til bruk i utvikling og
testing. Applikasjonen gir brukeren mulighet til å skrive en spesifikasjon av de testdataene som ønskes generert.
Spesifikasjonen blir validert mot forretningsregler og gyldig input, og deretter genereres et xml-dokument med data som
samsvarer med den angitte spesifikasjonen, samt eventuelle felter som har blitt automatisk fylt ut
(f.eks. fødselsnummer, organisasjonsnummer, beløp) i den grad brukeren ikke angir disse feltene selv i spesifikasjonen.

Testdataene som genereres er syntetiske, og er basert på test-personer og -enheter som hentes fra *Tenor*.

Denne aktuelle løsningen som er publisert på Github er en forenklet og nedkortet versjon av den Testdatageneratoren som
brukes internt i Skatteetaten, og denne løsningen er derfor ment å brukes kun som et utgangspunkt og som eksempelkode
for andre som ønsker å lage og videreutvikle en tilsvarende løsning innenfor sitt domene. Denne forenklede versjonen
gir mulighet for å generere dokumenter kun av typen "SaldoRente", men koden kan utvides til å generere en rekke andre
dokumenttyper.

Testdatageneratoren består av tre komponenter:

- [tdg-frontend](https://github.com/Skatteetaten/tdg-frontend)
- [tdg-kotlin-compiler-server](https://github.com/Skatteetaten/tdg-kotlin-compiler-server)
- [tdg-backend](https://github.com/Skatteetaten/tdg-backend)

### Funksjonalitet og flyt mellom komponenter

- **tdg-frontend** er brukergrensesnittet som lar brukeren definere en spesifikasjon for testdata som skal genereres.
  Spesifikasjonen skrives som et internt utviklet DSL (kalt "TestData Spesifikasjonsspråk"/TDSS) basert på Kotlin.
  TDSS'en er definert i *tdg-backend*. Frontenden gjør kall til tdg-kotlin-compiler-server for løpende validering av
  spesifikasjonen, og fra frontenden sendes deretter spesifikasjonen til tdg-backend.
- **tdg-backend** består av to moduler.
    - **_tdss_** definerer domenespråket (TDSS) og bestemmer hva som er lov å spesifisere i frontenden.
    - **_testdatagenerator_** tar imot spesifikasjonen, genererer testdata i xml-format og returnerer denne til
      frontenden.
- **tdg-kotlin-compiler-server** er en tjeneste som gir highlighting, autocomplete og feilmeldinger for spesifikasjonen
  som skrives i frontenden. Valideringen gjøres mot tdss-modulen i *tdg-backend*. *tdg-kotlin-compiler-server* er en forket
  versjon av [Jetbrains sin Kotlin compiler server](https://github.com/JetBrains/kotlin-compiler-server), som deretter
  har blitt tilpasset Skatteetatens domene.


*testdatagenerator* og *tdg-kotlin-compiler-server* bruker *tdss* som bibliotek. Innsendingene er tekst i TDSS streng-format,
som kan komme fra frontend eller HTTP-klienter. *korrelasjonsId* brukes for å hente assosierte dokumenter via HTTP.

![Testdata flyt](docs/flytdiagram.png)


## tdg-backend

*tdg-backend* er et eksempel på hvordan man kan lage en generator for syntetiske testdata i form av XML-dokumenter.
Den tar inn *Testdata Spesifikasjonsspråk* (TDSS), hvor man spesifiserer ønskede verdier i dokumentet.
*tdg-backend* vil generere dokumenter hvor alle verdier er tilfeldige, utenom det som er spesifisert.

En av styrkene til *tdg-backend* er at dataen som genereres er konsistente med syntetiske data fra [Tenor](https://www.skatteetaten.no/testdata/).


### tdss

*TDSS* er et Kotlin-basert Domain Specific Language (DSL) som er utviklet for å beskrive syntetiske testdata på en enkel og deklarativ måte.
Modulen inneholder:

- Annotasjoner: `@TdssInput` er en del av DSL-definisjonen og markerer hvilke felter i domenemodellen som er tilgjengelige for brukeren i TDSS.
  Bare felter med denne annotasjonen kan settes i spesifikasjonen.
- Parsing: konverterer TDSS-strenger til objektstruktur.
- Intern validering: Regler som sjekker logiske brister (f.eks. at "beløp" ikke er negativt, eller at datoer er i riktig rekkefølge) før man forsøker å generere data.

### testdatagenerator

Dette er selve Spring Boot-applikasjonen som syr sammen prosessen:

1) Kontroller: `DokumentController` tar imot forespørselen, og angir en korrelasjonsId knyttet til dokumentet.
2) Service: `DokumentService` tar imot spesifikasjonen, konverter TDSS til `Part`, genererer XML-dokumentet og lagrer det i et *map*-objekt hvor nøkkelen er korrelasjonsId.
3) Tenor Klient: For å skape realistiske testdata benytter applikasjonen en mock av systemet Tenor. Her hentes gyldige, syntetiske fødselsnummer/organisasjonsnummer og navn
   som samsvarer med kravene i spesifikasjonen.
4) Domeneutfyller: Dette er en sentral komponent som fyller "hullene" i spesifikasjonen. Dersom brukeren kun ber om "en person med renteinntekter",
   vil domeneutfylleren automatisk generere obligatoriske felter som mangler basert på tilfeldige men gyldige verdier.
5) XSD og JAXB: Prosjektet bruker jaxb2-maven-plugin for å generere Java-klasser automatisk fra de offisielle XSD-skjemaene for skattemeldinger.
   Data fra domeneutfylleren mappes over i disse genererte klassene før de marshalles til XML.


## Testdata Spesifikasjonsspråk (TDSS)

I selve spesifikasjonen tillater vi bruk av norske tegn (*æ*, *ø*, *å*).

### En enkel saldorente oppgave

Her defineres en person med et spesifikt fødselsnummer og en saldorente oppgave med ett innskudd. Øvrige felter genereres automatisk av systemet.

```kotlin
person {
    fødselsnummer = "26890296253"
    saldoRente {
        inntektsår = 2023
        innskudd {
            beløp = 12610
        }
    }
}
```

### Flere oppgaver under samme person


```kotlin
person {
    fødselsnummer = "26890296253"
    saldoRente {
        inntektsår = 2024
        innskudd {
            beløp = 12010
        }
    }
    saldoRente {
        inntektsår = 2023
        innskudd {
            beløp = 13200
        }
    }
    saldoRente {
        inntektsår = 2022
        innskudd {
            beløp = 11750
        }
    }
}
```

### Streng-format

Når *TDSS* sendes til backend via HTTP, er det i form av en streng formatert på følgende måte:

```
"person { saldoRente { inntektsår = 2024 ; innskudd { beløp = 2000 } } }"
```

## Tenor Klient

Gyldige fødselsnummer og organisasjonsnummer hentes 
fra Tenor gjennom [TenorKlient.java](testdatagenerator/src/main/java/no/skatteetaten/rst/testdatagenerator/tenor/TenorKlient.java).
For å gjøre det lettere å teste lokalt er det laget en mock implementasjon av Tenor-klienten, som har følgende gyldige
personer og organisasjoner lagret.

Det må [søkes om tilgang](https://skatteetaten.github.io/testnorge-tenor-dokumentasjon/) før det kan opprettes en ekte kobling mot Tenor.

| Fødselsnummer | Organisasjonsnummer |
|:--------------|:--------------------|
| 26890296253   | 315403618           |
| 21929897614   | 313596443           |
| 30897898051   | 314322045           |
| 23838197175   | 315408938           |
| 28885497693   | 310527335           |
| 10919396674   | 310521051           |
| 21884995504   | 213589202           |
| 07926399971   | 311775626           |
| 12907599866   | 312306417           |
| 28887199193   | 310643106           |


## Oppstart

*tdg-backend* kjører på port `8081` når den kjører på *dev* profil. Dette er for å unngå portkonflikt med *tdg-kotlin-compiler-server*.
Den bruker også [TenorKlientMock](testdatagenerator/src/main/java/no/skatteetaten/rst/testdatagenerator/tenor/TenorKlientMock.java) som Tenor-klient,
så *tdg-backend* kan testes uten å ha en integrasjon mot *Tenor*.

### Kjøre *tdg-backend* med Maven

1) Generer nødvendige klasser

```shell
mvn clean install
```

2) Kjør *testdatagenerator* i enten *dev* eller *prod* profil.

```shell
mvn -pl testdatagenerator spring-boot:run -Dspring-boot.run.profiles=dev
```

### Testing uten frontend

Testing uten frontend kan gjøres med *curl* eller ved hjelp av *IntelliJ* sin HTTP-syntaks.

#### CURL

Et enkelt eksempel på et HTTP-kall med *curl*, hvor saldoRente blir sendt inn.

```shell
korrelasjonsId=$(curl -X POST "http://localhost:8081/api/dokumenter/tdss" \
  -H "Content-Type: application/json" \
  -d '{
        "spesifikasjon": "person { fødselsnummer = \"26890296253\" ; saldoRente { inntektsår = 2024 ; innskudd { beløp = 2000 } } }"
      }')
```

Man kan hente dokumentet gitt at man har korrelasjonsId for dokumentet.

```shell
curl -X GET "http://localhost:8081/api/dokumenter/${korrelasjonsId}" | jq -r '.[0].dokument'
```

#### IntelliJ HTTP-Syntaks

[**testdatagenerator-dev.http**](testdatagenerator/http/testdatagenerator-dev.http) kan brukes til å teste ut *testdatagenerator* ved hjelp av *IntelliJ* sin HTTP-syntaks.
Husk å velge riktig environment.

### Testing med Swagger

[**Swagger**](http://localhost:8081/swagger-ui/index.html) kan også brukes til å teste *testdatagenerator*.


## Begreper og definisjoner


| Begrep             | Beskrivelse                                                                                                                                                                                                        |
|:-------------------|:-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| **RST**            | *Rike Syntetiske Testdata*. Navnet på gruppen hos *Skatteetaten* som har laget løsningen.                                                                                                                          |
| **TDSS**           | *Testdata Spesifikasjonsspråk*. Et DSL basert på Kotlin for å beskrive testdata strukturert.                                                                                                                       |
| **Tenor**          | En søkeløsning for å finne data fra det syntetiske Test-Norge.                                                                                                                                                     |
| **Part**           | En *part* er en *person* eller en *enhet*. De har begge identifikatornummer, altså fødselsnummer eller organisasjonsnummer.                                                                                        |
| **Person**         | En *person* kan spesifiseres med fødselsnummer. Navnet hentes fra Tenor før det brukes i XML-dokumentet som genereres.                                                                                             |
| **Enhet**          | En *enhet* er en registrert næringsdrivende virksomhet. En enhet kan være et enkeltmannsforetak, et aksjeselskap eller et ansvarlig selskap.                                                                       |
| **Domeneutfyller** | Logikklag i backend. Hvis brukeren kun spesifiserer et fåtall felter (f.eks. kun beløp), vil domeneutfylleren sørge for at resten av datamodellen fylles med gyldige, tilfeldige verdier slik at XML-en validerer. |

