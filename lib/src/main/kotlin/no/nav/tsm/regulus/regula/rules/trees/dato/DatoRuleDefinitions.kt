package no.nav.tsm.regulus.regula.rules.trees.dato

import no.nav.tsm.regulus.regula.dsl.RuleOutcome
import no.nav.tsm.regulus.regula.dsl.RuleStatus

internal enum class DatoRule {
    FREMDATERT,
    TILBAKEDATERT_MER_ENN_3_AR,
    TOTAL_VARIGHET_OVER_ETT_AAR;

    enum class Outcomes(
        override val rule: String,
        override val status: RuleStatus,
        override val messageForSender: String,
        override val messageForUser: String,
    ) : RuleOutcome {
        FREMDATERT(
            rule = "FREMDATERT",
            status = RuleStatus.INVALID,
            messageForSender =
                "Sykmeldingen kan ikke rettes, det må skrives en ny. " +
                    "Pasienten har fått beskjed om å vente på ny sykmelding fra deg. Grunnet følgende:" +
                    "Hvis sykmeldingen er fremdatert mer enn 30 dager etter behandletDato avvises meldingen.",
            messageForUser = "Sykmeldingen er datert mer enn 30 dager fram i tid.",
        ),
        TILBAKEDATERT_MER_ENN_3_AR(
            rule = "TILBAKEDATERT_MER_ENN_3_AR",
            status = RuleStatus.INVALID,
            messageForSender =
                "Sykmeldingen kan ikke rettes, det må skrives en ny. " +
                    "Pasienten har fått beskjed om å vente på ny sykmelding fra deg. Grunnet følgende: " +
                    "Sykmeldinges fom-dato er mer enn 3 år tilbake i tid.",
            messageForUser = "Startdatoen er mer enn tre år tilbake.",
        ),
        TOTAL_VARIGHET_OVER_ETT_AAR(
            rule = "TOTAL_VARIGHET_OVER_ETT_AAR",
            status = RuleStatus.INVALID,
            messageForSender =
                "Sykmeldingen kan ikke rettes, det må skrives en ny. " +
                    "Pasienten har fått beskjed om å vente på ny sykmelding fra deg. Grunnet følgende:" +
                    "Sykmeldingen første fom og siste tom har ein varighet som er over 1 år",
            messageForUser = "Den kan ikke ha en varighet på over ett år.",
        ),
    }
}
