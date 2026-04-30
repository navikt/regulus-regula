package no.nav.tsm.regulus.regula.rules.trees.legeSuspensjon

import no.nav.tsm.regulus.regula.dsl.RuleOutcome
import no.nav.tsm.regulus.regula.dsl.RuleStatus

internal enum class LegeSuspensjonRule {
    BEHANDLER_SUSPENDERT;

    enum class Outcomes(
        override val status: RuleStatus,
        override val messageForSender: String,
        override val messageForUser: String,
    ) : RuleOutcome {
        BEHANDLER_SUSPENDERT(
            status = RuleStatus.INVALID,
            messageForSender =
                "Behandler er suspendert av NAV på konsultasjonstidspunkt. Pasienten har fått beskjed.",
            messageForUser = "Den som sykmeldte deg har mistet retten til å skrive sykmeldinger.",
        )
    }
}
