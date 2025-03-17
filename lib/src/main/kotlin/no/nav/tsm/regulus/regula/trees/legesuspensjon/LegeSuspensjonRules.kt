package no.nav.tsm.regulus.regula.trees.legesuspensjon

import no.nav.tsm.regulus.regula.dsl.RuleOutput
import no.nav.tsm.regulus.regula.executor.TreeExecutor

class LegeSuspensjonRules(legeSuspensjonPayload: LegeSuspensjonPayload) :
    TreeExecutor<LegeSuspensjonRule, LegeSuspensjonPayload>(
        legeSuspensjonRuleTree,
        legeSuspensjonPayload,
    ) {
    override fun getRule(rule: LegeSuspensjonRule) = getLegeSuspensjonRule(rule)
}

fun getLegeSuspensjonRule(rule: LegeSuspensjonRule): LegeSuspensjonRuleFn =
    when (rule) {
        LegeSuspensjonRule.BEHANDLER_SUSPENDERT -> Rules.behandlerSuspendert
    }

private typealias LegeSuspensjonRuleFn =
    (behandlerSuspendert: LegeSuspensjonPayload) -> RuleOutput<LegeSuspensjonRule>

private val Rules =
    object {
        val behandlerSuspendert: LegeSuspensjonRuleFn = { payload ->
            val behandlerSuspendert = payload.behandlerSuspendert

            RuleOutput(
                ruleInputs = mapOf("suspendert" to behandlerSuspendert),
                rule = LegeSuspensjonRule.BEHANDLER_SUSPENDERT,
                ruleResult = behandlerSuspendert,
            )
        }
    }
