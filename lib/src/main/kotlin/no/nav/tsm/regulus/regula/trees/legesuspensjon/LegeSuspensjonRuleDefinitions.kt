package no.nav.tsm.regulus.regula.trees.legesuspensjon

import no.nav.tsm.regulus.regula.executor.RuleDefinitions
import no.nav.tsm.regulus.regula.executor.RuleOutcome
import no.nav.tsm.regulus.regula.executor.RuleStatus

enum class LegeSuspensjonRule : RuleDefinitions {
    BEHANDLER_SUSPENDERT;

    override val Outcome: RuleOutcome
        get() = when (this) {
            BEHANDLER_SUSPENDERT -> RuleOutcome(
                rule = this.name,
                status = RuleStatus.INVALID,
                messageForSender = "Behandler er suspendert av NAV på konsultasjonstidspunkt. Pasienten har fått beskjed.",
                messageForUser = "Den som sykmeldte deg har mistet retten til å skrive sykmeldinger."
            )
        }
}
