package no.nav.tsm.regulus.regula.rules.trees.legeSuspensjon

import no.nav.tsm.regulus.regula.dsl.RuleOutput
import no.nav.tsm.regulus.regula.executor.TreeExecutor

internal class LegeSuspensjonRules(legeSuspensjonPayload: LegeSuspensjonRulePayload) :
    TreeExecutor<LegeSuspensjonRule, LegeSuspensjonRulePayload>(
        legeSuspensjonRuleTree,
        legeSuspensjonPayload,
    ) {
    override fun getRule(rule: LegeSuspensjonRule) = getLegeSuspensjonRule(rule)
}

private fun getLegeSuspensjonRule(rule: LegeSuspensjonRule): LegeSuspensjonRuleFn =
    when (rule) {
        LegeSuspensjonRule.SYKMELDER_SUSPENDERT -> Rules.sykmelderSuspendert
    }

private typealias LegeSuspensjonRuleFn =
    (sykmelderSuspendert: LegeSuspensjonRulePayload) -> RuleOutput<LegeSuspensjonRule>

private val Rules =
    object {
        val sykmelderSuspendert: LegeSuspensjonRuleFn = { payload ->
            val sykmelderSuspendert = payload.sykmelderSuspendert

            RuleOutput(
                ruleInputs = mapOf("suspendert" to sykmelderSuspendert),
                rule = LegeSuspensjonRule.SYKMELDER_SUSPENDERT,
                ruleResult = sykmelderSuspendert,
            )
        }
    }
