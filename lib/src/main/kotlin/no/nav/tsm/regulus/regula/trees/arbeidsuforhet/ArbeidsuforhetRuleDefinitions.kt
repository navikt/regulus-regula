package no.nav.tsm.regulus.regula.trees.arbeidsuforhet

import no.nav.tsm.regulus.regula.executor.RuleOutcome
import no.nav.tsm.regulus.regula.executor.RuleStatus

internal enum class ArbeidsuforhetRule {
    ICPC_2_Z_DIAGNOSE,
    HOVEDDIAGNOSE_MANGLER,
    FRAVAERSGRUNN_MANGLER,
    UGYLDIG_KODEVERK_FOR_HOVEDDIAGNOSE,
    UGYLDIG_KODEVERK_FOR_BIDIAGNOSE;

    enum class Outcomes(
        override val rule: String,
        override val status: RuleStatus,
        override val messageForSender: String,
        override val messageForUser: String,
    ) : RuleOutcome {
        ICPC_2_Z_DIAGNOSE(
            rule = "ICPC_2_Z_DIAGNOSE",
            status = RuleStatus.INVALID,
            messageForSender =
                "Angitt hoveddiagnose (z-diagnose) gir ikke rett til sykepenger. Pasienten har fått beskjed.",
            messageForUser = "Den må ha en gyldig diagnosekode som gir rett til sykepenger.",
        ),
        FRAVAERSGRUNN_MANGLER(
            rule = "HOVEDDIAGNOSE_ELLER_FRAVAERSGRUNN_MANGLER",
            status = RuleStatus.INVALID,
            messageForSender =
                "Sykmeldingen kan ikke rettes, det må skrives en ny. " +
                    "Pasienten har fått beskjed om å vente på ny sykmelding fra deg. Grunnet følgende:" +
                    "no.nav.tsm.regulus.regula.payload.Hoveddiagnose eller annen lovfestet fraværsgrunn mangler. ",
            messageForUser = "Den må ha en hoveddiagnose eller en annen gyldig fraværsgrunn.",
        ),
        UGYLDIG_KODEVERK_FOR_HOVEDDIAGNOSE(
            rule = "UGYLDIG_KODEVERK_FOR_HOVEDDIAGNOSE",
            status = RuleStatus.INVALID,
            messageForSender =
                "Sykmeldingen kan ikke rettes, det må skrives en ny. " +
                    "Pasienten har fått beskjed om å vente på ny sykmelding fra deg. Grunnet følgende:" +
                    "Kodeverk for hoveddiagnose er ikke angitt eller ukjent.",
            messageForUser = "Den må ha riktig kode for hoveddiagnose.",
        ),
        UGYLDIG_KODEVERK_FOR_BIDIAGNOSE(
            rule = "UGYLDIG_KODEVERK_FOR_BIDIAGNOSE",
            status = RuleStatus.INVALID,
            messageForSender =
                "Sykmeldingen kan ikke rettes, det må skrives en ny. " +
                    "Pasienten har fått beskjed om å vente på ny sykmelding fra deg. Grunnet følgende:" +
                    "Kodeverk for bidiagnose er ikke angitt eller ukjent.",
            messageForUser = "Det er brukt eit ukjent kodeverk for bidiagnosen.",
        ),
    }
}
