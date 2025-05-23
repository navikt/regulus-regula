package no.nav.tsm.regulus.regula.rules.trees.pasientUnder13

import no.nav.tsm.regulus.regula.dsl.RuleOutcome
import no.nav.tsm.regulus.regula.dsl.RuleStatus

internal enum class PasientUnder13Rule {
    PASIENT_YNGRE_ENN_13;

    enum class Outcomes(
        override val status: RuleStatus,
        override val messageForSender: String,
        override val messageForUser: String,
    ) : RuleOutcome {
        PASIENT_YNGRE_ENN_13(
            status = RuleStatus.INVALID,
            messageForSender = "Pasienten er under 13 år. Sykmelding kan ikke benyttes.",
            messageForUser = "Pasienten er under 13 år. Sykmelding kan ikke benyttes.",
        )
    }
}
