package no.nav.tsm.regulus.regula.rules.trees.validering

import no.nav.tsm.regulus.regula.executor.RuleOutcome
import no.nav.tsm.regulus.regula.executor.RuleStatus

internal enum class ValideringRule {
    UGYLDIG_REGELSETTVERSJON,
    MANGLENDE_DYNAMISKE_SPOERSMAL_VERSJON2_UKE_39,
    UGYLDIG_ORGNR_LENGDE,
    AVSENDER_FNR_ER_SAMME_SOM_PASIENT_FNR,
    BEHANDLER_FNR_ER_SAMME_SOM_PASIENT_FNR;

    enum class Outcomes(
        override val rule: String,
        override val status: RuleStatus,
        override val messageForSender: String,
        override val messageForUser: String,
    ) : RuleOutcome {
        UGYLDIG_REGELSETTVERSJON(
            rule = "UGYLDIG_REGELSETTVERSJON",
            status = RuleStatus.INVALID,
            messageForSender =
                "Sykmeldingen kan ikke rettes, det må skrives en ny. " +
                    "Pasienten har fått beskjed om å vente på ny sykmelding fra deg. Grunnet følgende:" +
                    "Feil regelsett er brukt i sykmeldingen.",
            messageForUser = "Det er brukt en versjon av sykmeldingen som ikke lenger er gyldig.",
        ),
        MANGLENDE_DYNAMISKE_SPOERSMAL_VERSJON2_UKE_39(
            rule = "MANGLENDE_DYNAMISKE_SPOERSMAL_VERSJON2_UKE_39",
            status = RuleStatus.INVALID,
            messageForSender =
                "Sykmeldingen kan ikke rettes, det må skrives en ny." +
                    " Pasienten har fått beskjed om å vente på ny sykmelding fra deg. Grunnet følgende:" +
                    "Utdypende opplysninger som kreves ved uke 39 mangler. ",
            messageForUser =
                "Sykmeldingen mangler utdypende opplysninger som kreves når " +
                    "sykefraværet er lengre enn 39 uker til sammen.",
        ),
        UGYLDIG_ORGNR_LENGDE(
            rule = "UGYLDIG_ORGNR_LENGDE",
            status = RuleStatus.INVALID,
            messageForSender =
                "Sykmeldingen kan ikke rettes, det må skrives en ny. " +
                    "Pasienten har fått beskjed om å vente på ny sykmelding fra deg. Grunnet følgende:" +
                    "Feil format på organisasjonsnummer. Dette skal være 9 sifre.",
            messageForUser = "Den må ha riktig organisasjonsnummer.",
        ),
        AVSENDER_FNR_ER_SAMME_SOM_PASIENT_FNR(
            rule = "AVSENDER_FNR_ER_SAMME_SOM_PASIENT_FNR",
            status = RuleStatus.INVALID,
            messageForSender =
                "Sykmeldingen kan ikke rettes, " +
                    "Pasienten har fått beskjed, den ble avvist grunnet følgende:" +
                    "Avsender fnr er det samme som pasient fnr",
            messageForUser = "Den som signert sykmeldingen er også pasient.",
        ),
        BEHANDLER_FNR_ER_SAMME_SOM_PASIENT_FNR(
            rule = "BEHANDLER_FNR_ER_SAMME_SOM_PASIENT_FNR",
            status = RuleStatus.INVALID,
            messageForSender =
                "Sykmeldingen kan ikke rettes." +
                    " Pasienten har fått beskjed, den ble avvist grunnet følgende:" +
                    "no.nav.tsm.regulus.regula.trees.hpr.Behandler fnr er det samme som pasient fnr",
            messageForUser = "Den som er behandler av sykmeldingen er også pasient.",
        ),
    }
}
