package no.nav.tsm.regulus.regula.trees.legesuspensjon

import no.nav.tsm.regulus.regula.dsl.RuleOutput

enum class LegeSuspensjonRules {
    BEHANDLER_SUSPENDERT,
}

private typealias LegeSuspensjonRuleFn = (behandlerSuspendert: Boolean) -> RuleOutput<LegeSuspensjonRules>

fun getRule(rule: LegeSuspensjonRules): LegeSuspensjonRuleFn = when (rule) {
    LegeSuspensjonRules.BEHANDLER_SUSPENDERT -> Rules.behandlerSuspendert
}

private val Rules = object {
    val behandlerSuspendert: LegeSuspensjonRuleFn = { behandlerSuspendert ->
        RuleOutput(
            ruleInputs = mapOf("suspendert" to behandlerSuspendert),
            rule = LegeSuspensjonRules.BEHANDLER_SUSPENDERT,
            ruleResult = behandlerSuspendert,
        )
    }
}
