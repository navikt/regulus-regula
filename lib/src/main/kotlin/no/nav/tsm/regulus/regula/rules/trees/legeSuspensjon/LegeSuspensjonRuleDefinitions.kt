package no.nav.tsm.regulus.regula.rules.trees.legeSuspensjon

import no.nav.tsm.regulus.regula.dsl.RuleOutcome
import no.nav.tsm.regulus.regula.dsl.RuleStatus

internal enum class LegeSuspensjonRule {
    SYKMELDER_SUSPENDERT;

    enum class Outcomes(
        override val status: RuleStatus,
        override val messageForSender: String,
        override val messageForUser: String,
    ) : RuleOutcome {
        SYKMELDER_SUSPENDERT(
            status = RuleStatus.INVALID,
            messageForSender =
                "Sykmelder er suspendert av NAV på konsultasjonstidspunkt. Pasienten har fått beskjed.",
            messageForUser = "Den som sykmeldte deg har mistet retten til å skrive sykmeldinger.",
        )
    }
}
