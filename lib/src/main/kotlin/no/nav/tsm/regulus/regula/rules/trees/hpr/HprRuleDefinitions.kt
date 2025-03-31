package no.nav.tsm.regulus.regula.rules.trees.hpr

import no.nav.tsm.regulus.regula.dsl.RuleOutcome
import no.nav.tsm.regulus.regula.dsl.RuleStatus

internal enum class HprRule {
    BEHANDLER_GYLIDG_I_HPR,
    BEHANDLER_HAR_AUTORISASJON_I_HPR,
    BEHANDLER_ER_LEGE_I_HPR,
    BEHANDLER_ER_TANNLEGE_I_HPR,
    BEHANDLER_ER_MANUELLTERAPEUT_I_HPR,
    BEHANDLER_ER_FT_MED_TILLEGSKOMPETANSE_I_HPR,
    BEHANDLER_ER_KI_MED_TILLEGSKOMPETANSE_I_HPR,
    SYKEFRAVAR_OVER_12_UKER;

    enum class Outcomes(
        override val rule: String,
        override val status: RuleStatus,
        override val messageForSender: String,
        override val messageForUser: String,
    ) : RuleOutcome {
        BEHANDLER_IKKE_GYLDIG_I_HPR(
            rule = "BEHANDLER_IKKE_GYLDIG_I_HPR",
            status = RuleStatus.INVALID,
            messageForSender =
                "no.nav.tsm.regulus.regula.trees.hpr.Behandler er ikke gyldig i HPR på konsultasjonstidspunkt. Pasienten har fått beskjed.",
            messageForUser = "Den som skrev sykmeldingen manglet autorisasjon.",
        ),
        BEHANDLER_MANGLER_AUTORISASJON_I_HPR(
            rule = "BEHANDLER_MANGLER_AUTORISASJON_I_HPR",
            status = RuleStatus.INVALID,
            messageForSender =
                "no.nav.tsm.regulus.regula.trees.hpr.Behandler har ikke gyldig autorisasjon i HPR. Pasienten har fått beskjed.",
            messageForUser = "Den som skrev sykmeldingen manglet autorisasjon.",
        ),
        BEHANDLER_IKKE_LE_KI_MT_TL_FT_I_HPR(
            rule = "BEHANDLER_IKKE_LE_KI_MT_TL_FT_I_HPR",
            status = RuleStatus.INVALID,
            messageForSender =
                "no.nav.tsm.regulus.regula.trees.hpr.Behandler finnes i HPR, men er ikke lege, kiropraktor, fysioterapeut, " +
                    "manuellterapeut eller tannlege. Pasienten har fått beskjed.",
            messageForUser = "Den som skrev sykmeldingen manglet autorisasjon.",
        ),
        BEHANDLER_MT_FT_KI_OVER_12_UKER(
            rule = "BEHANDLER_MT_FT_KI_OVER_12_UKER",
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
