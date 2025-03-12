package no.nav.tsm.regulus.regula.trees.legesuspensjon

import no.nav.tsm.regulus.regula.dsl.RuleOutput

typealias Rule<T> = (behandlerSuspendert: Boolean) -> RuleOutput<T>

typealias LegeSuspensjonRule = Rule<LegeSuspensjonRules>

val behandlerSuspendert: LegeSuspensjonRule = { behandlerSuspendert ->
    RuleOutput(
        ruleInputs = mapOf("suspendert" to behandlerSuspendert),
        rule = LegeSuspensjonRules.BEHANDLER_SUSPENDERT,
        ruleResult = behandlerSuspendert,
    )
}
