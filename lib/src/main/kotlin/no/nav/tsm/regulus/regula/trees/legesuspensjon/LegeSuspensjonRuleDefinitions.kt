package no.nav.tsm.regulus.regula.trees.legesuspensjon

import no.nav.tsm.regulus.regula.executor.RuleOutcome
import no.nav.tsm.regulus.regula.executor.RuleStatus

enum class LegeSuspensjonRule {
    BEHANDLER_SUSPENDERT;

    enum class Outcomes(
        override val rule: String,
        override val status: RuleStatus,
        override val messageForSender: String,
        override val messageForUser: String,
    ) : RuleOutcome {
        BEHANDLER_SUSPENDERT(
            rule = "BEHANDLER_SUSPENDERT",
            status = RuleStatus.INVALID,
            messageForSender =
                "no.nav.tsm.regulus.regula.trees.hpr.Behandler er suspendert av NAV på konsultasjonstidspunkt. Pasienten har fått beskjed.",
            messageForUser = "Den som sykmeldte deg har mistet retten til å skrive sykmeldinger.",
        )
    }
}
