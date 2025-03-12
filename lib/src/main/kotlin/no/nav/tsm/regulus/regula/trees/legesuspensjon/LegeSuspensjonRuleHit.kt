package no.nav.tsm.regulus.regula.trees.legesuspensjon

import no.nav.tsm.regulus.regula.executor.RuleHit
import no.nav.tsm.regulus.regula.executor.RuleStatus

enum class LegeSuspensjonRuleHit(
    val ruleHit: RuleHit,
) {
    BEHANDLER_SUSPENDERT(
        ruleHit = RuleHit(
            rule = "BEHANDLER_SUSPENDERT",
            status = RuleStatus.INVALID,
            messageForSender = "Behandler er suspendert av NAV på konsultasjonstidspunkt. Pasienten har fått beskjed.",
            messageForUser = "Den som sykmeldte deg har mistet retten til å skrive sykmeldinger.",
        ),
    ),
}
