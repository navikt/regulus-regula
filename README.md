# regulus-regula

Dette er en Kotlin-modul som representerer alle regel-utprøvingene vi gjør i på hver sykmelding vi mottar.

Denne modulen skal i hovedsak kun brukes av:

* regulus-maximus (i mottaket)
* syk-inn-api (i nytt sykmeldings-løp)

## Regeltre

Disse grafene er generert opp av samme kode-struktur som selve reglene er definert med.

Endringer i regel-implementasjonene vil derfor også reflekteres i disse grafene.

<!-- RULE_MARKER_START -->
## 0. Lege suspensjon

```mermaid
graph TD
    root(BEHANDLER_SUSPENDERT) -->|Yes| root_BEHANDLER_SUSPENDERT_INVALID(INVALID):::invalid
    root(BEHANDLER_SUSPENDERT) -->|No| root_BEHANDLER_SUSPENDERT_OK(OK):::ok
    classDef ok fill:#c3ff91,stroke:#004a00,color: black;
    classDef invalid fill:#ff7373,stroke:#ff0000,color: black;
    classDef manuell fill:#ffe24f,stroke:#ffd500,color: #473c00;
```



<!-- RULE_MARKER_END -->