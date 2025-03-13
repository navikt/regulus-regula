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


## 1. Validation

```mermaid
graph TD
    root(UGYLDIG_REGELSETTVERSJON) -->|Yes| root_UGYLDIG_REGELSETTVERSJON_INVALID(INVALID):::invalid
    root(UGYLDIG_REGELSETTVERSJON) -->|No| root_UGYLDIG_REGELSETTVERSJON_MANGLENDE_DYNAMISKE_SPOERSMAL_VERSJON2_UKE_39(MANGLENDE_DYNAMISKE_SPOERSMAL_VERSJON2_UKE_39)
    root_UGYLDIG_REGELSETTVERSJON_MANGLENDE_DYNAMISKE_SPOERSMAL_VERSJON2_UKE_39(MANGLENDE_DYNAMISKE_SPOERSMAL_VERSJON2_UKE_39) -->|Yes| root_UGYLDIG_REGELSETTVERSJON_MANGLENDE_DYNAMISKE_SPOERSMAL_VERSJON2_UKE_39_INVALID(INVALID):::invalid
    root_UGYLDIG_REGELSETTVERSJON_MANGLENDE_DYNAMISKE_SPOERSMAL_VERSJON2_UKE_39(MANGLENDE_DYNAMISKE_SPOERSMAL_VERSJON2_UKE_39) -->|No| root_UGYLDIG_REGELSETTVERSJON_MANGLENDE_DYNAMISKE_SPOERSMAL_VERSJON2_UKE_39_UGYLDIG_ORGNR_LENGDE(UGYLDIG_ORGNR_LENGDE)
    root_UGYLDIG_REGELSETTVERSJON_MANGLENDE_DYNAMISKE_SPOERSMAL_VERSJON2_UKE_39_UGYLDIG_ORGNR_LENGDE(UGYLDIG_ORGNR_LENGDE) -->|Yes| root_UGYLDIG_REGELSETTVERSJON_MANGLENDE_DYNAMISKE_SPOERSMAL_VERSJON2_UKE_39_UGYLDIG_ORGNR_LENGDE_INVALID(INVALID):::invalid
    root_UGYLDIG_REGELSETTVERSJON_MANGLENDE_DYNAMISKE_SPOERSMAL_VERSJON2_UKE_39_UGYLDIG_ORGNR_LENGDE(UGYLDIG_ORGNR_LENGDE) -->|No| root_UGYLDIG_REGELSETTVERSJON_MANGLENDE_DYNAMISKE_SPOERSMAL_VERSJON2_UKE_39_UGYLDIG_ORGNR_LENGDE_AVSENDER_FNR_ER_SAMME_SOM_PASIENT_FNR(AVSENDER_FNR_ER_SAMME_SOM_PASIENT_FNR)
    root_UGYLDIG_REGELSETTVERSJON_MANGLENDE_DYNAMISKE_SPOERSMAL_VERSJON2_UKE_39_UGYLDIG_ORGNR_LENGDE_AVSENDER_FNR_ER_SAMME_SOM_PASIENT_FNR(AVSENDER_FNR_ER_SAMME_SOM_PASIENT_FNR) -->|Yes| root_UGYLDIG_REGELSETTVERSJON_MANGLENDE_DYNAMISKE_SPOERSMAL_VERSJON2_UKE_39_UGYLDIG_ORGNR_LENGDE_AVSENDER_FNR_ER_SAMME_SOM_PASIENT_FNR_INVALID(INVALID):::invalid
    root_UGYLDIG_REGELSETTVERSJON_MANGLENDE_DYNAMISKE_SPOERSMAL_VERSJON2_UKE_39_UGYLDIG_ORGNR_LENGDE_AVSENDER_FNR_ER_SAMME_SOM_PASIENT_FNR(AVSENDER_FNR_ER_SAMME_SOM_PASIENT_FNR) -->|No| root_UGYLDIG_REGELSETTVERSJON_MANGLENDE_DYNAMISKE_SPOERSMAL_VERSJON2_UKE_39_UGYLDIG_ORGNR_LENGDE_AVSENDER_FNR_ER_SAMME_SOM_PASIENT_FNR_BEHANDLER_FNR_ER_SAMME_SOM_PASIENT_FNR(BEHANDLER_FNR_ER_SAMME_SOM_PASIENT_FNR)
    root_UGYLDIG_REGELSETTVERSJON_MANGLENDE_DYNAMISKE_SPOERSMAL_VERSJON2_UKE_39_UGYLDIG_ORGNR_LENGDE_AVSENDER_FNR_ER_SAMME_SOM_PASIENT_FNR_BEHANDLER_FNR_ER_SAMME_SOM_PASIENT_FNR(BEHANDLER_FNR_ER_SAMME_SOM_PASIENT_FNR) -->|Yes| root_UGYLDIG_REGELSETTVERSJON_MANGLENDE_DYNAMISKE_SPOERSMAL_VERSJON2_UKE_39_UGYLDIG_ORGNR_LENGDE_AVSENDER_FNR_ER_SAMME_SOM_PASIENT_FNR_BEHANDLER_FNR_ER_SAMME_SOM_PASIENT_FNR_INVALID(INVALID):::invalid
    root_UGYLDIG_REGELSETTVERSJON_MANGLENDE_DYNAMISKE_SPOERSMAL_VERSJON2_UKE_39_UGYLDIG_ORGNR_LENGDE_AVSENDER_FNR_ER_SAMME_SOM_PASIENT_FNR_BEHANDLER_FNR_ER_SAMME_SOM_PASIENT_FNR(BEHANDLER_FNR_ER_SAMME_SOM_PASIENT_FNR) -->|No| root_UGYLDIG_REGELSETTVERSJON_MANGLENDE_DYNAMISKE_SPOERSMAL_VERSJON2_UKE_39_UGYLDIG_ORGNR_LENGDE_AVSENDER_FNR_ER_SAMME_SOM_PASIENT_FNR_BEHANDLER_FNR_ER_SAMME_SOM_PASIENT_FNR_OK(OK):::ok
    classDef ok fill:#c3ff91,stroke:#004a00,color: black;
    classDef invalid fill:#ff7373,stroke:#ff0000,color: black;
    classDef manuell fill:#ffe24f,stroke:#ffd500,color: #473c00;
```



<!-- RULE_MARKER_END -->

## Strukturen på et regel-tre

Regeleksekveringsmotoren forventer at reglene består av:

### Overordnet definisjoner

* Et enum som beskriver alle regel-navnene
  * `enum FooBarRules { ... }`
* Et enum som beskriver alle regel-utfall
  * `enum class FooBarRuleOutcomes: RuleOutcome { ... }`
* Et `tree` som implementerer alle reglene, og deres yes/no utfall
  * `val fooBarTree = treetree<LegeSuspensjonRules, RuleResult>(LegeSuspensjonRules.FirstRule) { ... }`

### Eksekveringsspesifikt

* En data-klasse som definerer hvilke verdier dette regel-treet trenger
  * `data class FooBarPayload(...)`
* En klasse som implementerer binder sammen regel-treet og payload
  * `class FooBarRuleEvaluator(...): RuleExecution<FooBarRules> { ... }`
* Reglene!
  * En funksjon som sørger for at alle funksjonene er implementert
    * `fun getRule(rules: ValidationRules): ValidationRuleFn = when { ... }`
  * Et sett med pure functions som implementerer hver regel
    * `typealias FooBarRuleFn = (payload: FooBarPayload) -> RuleOutput<FooBarRules>`
    * `val fooBarRule1: FooBarRuleFn = { payload -> ... }`