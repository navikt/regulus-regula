package no.nav.tsm.regulus.regula.trees.legesuspensjon

import no.nav.tsm.regulus.regula.dsl.RuleOutput

private typealias LegeSuspensjonRuleFn = (behandlerSuspendert: Boolean) -> RuleOutput<LegeSuspensjonRule>

fun getRule(rule: LegeSuspensjonRule): LegeSuspensjonRuleFn = when (rule) {
    LegeSuspensjonRule.BEHANDLER_SUSPENDERT -> Rules.behandlerSuspendert
}

private val Rules = object {
    val behandlerSuspendert: LegeSuspensjonRuleFn = { behandlerSuspendert ->
        RuleOutput(
            ruleInputs = mapOf("suspendert" to behandlerSuspendert),
            rule = LegeSuspensjonRule.BEHANDLER_SUSPENDERT,
            ruleResult = behandlerSuspendert,
        )
    }
}
