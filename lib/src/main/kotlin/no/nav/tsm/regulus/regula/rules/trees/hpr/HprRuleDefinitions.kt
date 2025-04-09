package no.nav.tsm.regulus.regula.rules.trees.hpr

import no.nav.tsm.regulus.regula.dsl.RuleOutcome
import no.nav.tsm.regulus.regula.dsl.RuleStatus

internal enum class HprRule {
    SYKMELDER_FINNES_I_HPR,
    SYKMELDER_GYLDIG_I_HPR,
    SYKMELDER_HAR_AUTORISASJON_I_HPR,
    SYKMELDER_ER_LEGE_I_HPR,
    SYKMELDER_ER_TANNLEGE_I_HPR,
    SYKMELDER_ER_MANUELLTERAPEUT_I_HPR,
    SYKMELDER_ER_FT_MED_TILLEGSKOMPETANSE_I_HPR,
    SYKMELDER_ER_KI_MED_TILLEGSKOMPETANSE_I_HPR,
    SYKEFRAVAER_OVER_12_UKER;

    enum class Outcomes(
        override val status: RuleStatus,
        override val messageForSender: String,
        override val messageForUser: String,
    ) : RuleOutcome {
        SYKMELDER_IKKE_I_HPR(
            status = RuleStatus.INVALID,
            messageForSender =
                "Den som har skrevet sykmeldingen ble ikke funnet i Helsepersonellregisteret (HPR)",
            messageForUser =
                "Avsender fodselsnummer er ikke registert i Helsepersonellregisteret (HPR)",
        ),
        SYKMELDER_IKKE_GYLDIG_I_HPR(
            status = RuleStatus.INVALID,
            messageForSender =
                "Sykmelder er ikke gyldig i HPR på konsultasjonstidspunkt. Pasienten har fått beskjed.",
            messageForUser = "Den som skrev sykmeldingen manglet autorisasjon.",
        ),
        SYKMELDER_MANGLER_AUTORISASJON_I_HPR(
            status = RuleStatus.INVALID,
            messageForSender =
                "Sykmelder har ikke gyldig autorisasjon i HPR. Pasienten har fått beskjed.",
            messageForUser = "Den som skrev sykmeldingen manglet autorisasjon.",
        ),
        SYKMELDER_IKKE_LE_KI_MT_TL_FT_I_HPR(
            status = RuleStatus.INVALID,
            messageForSender =
                "Sykmelder finnes i HPR, men er ikke lege, kiropraktor, fysioterapeut, " +
                    "manuellterapeut eller tannlege. Pasienten har fått beskjed.",
            messageForUser = "Den som skrev sykmeldingen manglet autorisasjon.",
        ),
        SYKMELDER_MT_FT_KI_OVER_12_UKER(
            status = RuleStatus.INVALID,
            messageForSender =
                "Sykmeldingen er avvist fordi det totale sykefraværet overstiger 12 uker (du som KI/MT/FT " +
                    "kan ikke sykmelde utover 12 uker). Pasienten har fått beskjed.",
            messageForUser =
                "Sykmeldingen din er avvist fordi den som sykmeldte deg ikke kan skrive en sykmelding som " +
                    "gjør at sykefraværet ditt overstiger 12 uker",
        ),
    }
}
