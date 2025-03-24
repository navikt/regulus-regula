package no.nav.tsm.regulus.regula.trees.legeSuspensjon

import no.nav.tsm.regulus.regula.dsl.RuleOutput
import no.nav.tsm.regulus.regula.executor.TreeExecutor

class LegeSuspensjonRules(legeSuspensjonPayload: LegeSuspensjonRulePayload) :
    TreeExecutor<LegeSuspensjonRule, LegeSuspensjonRulePayload>(
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
    (behandlerSuspendert: LegeSuspensjonRulePayload) -> RuleOutput<LegeSuspensjonRule>

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
