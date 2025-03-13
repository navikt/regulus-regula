package no.nav.tsm.regulus.regula.trees.validation

import no.nav.tsm.regulus.regula.executor.RuleDefinitions
import no.nav.tsm.regulus.regula.executor.RuleOutcome
import no.nav.tsm.regulus.regula.executor.RuleStatus

enum class ValidationRule : RuleDefinitions {
    UGYLDIG_REGELSETTVERSJON,
    MANGLENDE_DYNAMISKE_SPOERSMAL_VERSJON2_UKE_39,
    UGYLDIG_ORGNR_LENGDE,
    AVSENDER_FNR_ER_SAMME_SOM_PASIENT_FNR,
    BEHANDLER_FNR_ER_SAMME_SOM_PASIENT_FNR;

    override val Outcome: RuleOutcome
        get() = when (this) {
            UGYLDIG_REGELSETTVERSJON -> RuleOutcome(
                rule = this.name,
                status = RuleStatus.INVALID,
                messageForSender = "Sykmeldingen kan ikke rettes, det må skrives en ny. " +
                        "Pasienten har fått beskjed om å vente på ny sykmelding fra deg. Grunnet følgende:" +
                        "Feil regelsett er brukt i sykmeldingen.",
                messageForUser =
                    "Det er brukt en versjon av sykmeldingen som ikke lenger er gyldig.",
            )

            MANGLENDE_DYNAMISKE_SPOERSMAL_VERSJON2_UKE_39 -> RuleOutcome(
                rule = this.name,
                status = RuleStatus.INVALID,
                messageForSender = "Sykmeldingen kan ikke rettes, det må skrives en ny." +
                        " Pasienten har fått beskjed om å vente på ny sykmelding fra deg. Grunnet følgende:" +
                        "Utdypende opplysninger som kreves ved uke 39 mangler. ",
                messageForUser =
                    "Sykmeldingen mangler utdypende opplysninger som kreves når " +
                            "sykefraværet er lengre enn 39 uker til sammen.",
            )

            UGYLDIG_ORGNR_LENGDE -> RuleOutcome(
                rule = this.name,
                status = RuleStatus.INVALID,
                messageForSender =
                    "Sykmeldingen kan ikke rettes, det må skrives en ny. " +
                            "Pasienten har fått beskjed om å vente på ny sykmelding fra deg. Grunnet følgende:" +
                            "Feil format på organisasjonsnummer. Dette skal være 9 sifre.",
                messageForUser = "Den må ha riktig organisasjonsnummer.",
            )

            AVSENDER_FNR_ER_SAMME_SOM_PASIENT_FNR -> RuleOutcome(
                rule = this.name,
                status = RuleStatus.INVALID,
                messageForSender =
                    "Sykmeldingen kan ikke rettes, " +
                            "Pasienten har fått beskjed, den ble avvist grunnet følgende:" +
                            "Avsender fnr er det samme som pasient fnr",
                messageForUser = "Den som signert sykmeldingen er også pasient.",
            )

            BEHANDLER_FNR_ER_SAMME_SOM_PASIENT_FNR -> RuleOutcome(
                rule = this.name,
                status = RuleStatus.INVALID,
                messageForSender =
                    "Sykmeldingen kan ikke rettes." +
                            " Pasienten har fått beskjed, den ble avvist grunnet følgende:" +
                            "Behandler fnr er det samme som pasient fnr",
                messageForUser = "Den som er behandler av sykmeldingen er også pasient.",
            )

        }
}
