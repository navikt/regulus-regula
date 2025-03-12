package no.nav.tsm.regulus.regula.trees.legesuspensjon

import no.nav.tsm.regulus.regula.dsl.RuleNode
import no.nav.tsm.regulus.regula.dsl.UtenJuridisk
import no.nav.tsm.regulus.regula.dsl.tree
import no.nav.tsm.regulus.regula.executor.RuleResult
import no.nav.tsm.regulus.regula.executor.RuleStatus

enum class LegeSuspensjonRules {
    BEHANDLER_SUSPENDERT,
}

val legeSuspensjonRuleTree = tree<LegeSuspensjonRules, RuleResult>(
    LegeSuspensjonRules.BEHANDLER_SUSPENDERT
) {
    yes(RuleStatus.INVALID, LegeSuspensjonRuleHit.BEHANDLER_SUSPENDERT)
    no(RuleStatus.OK)
} to UtenJuridisk

internal fun RuleNode<LegeSuspensjonRules, RuleResult>.yes(
    status: RuleStatus,
    ruleHit: LegeSuspensjonRuleHit? = null
) {
    yes(RuleResult(status, ruleHit?.ruleHit))
}

internal fun RuleNode<LegeSuspensjonRules, RuleResult>.no(
    status: RuleStatus,
    ruleHit: LegeSuspensjonRuleHit? = null
) {
    no(RuleResult(status, ruleHit?.ruleHit))
}

fun getRule(rules: LegeSuspensjonRules): Rule<LegeSuspensjonRules> {
    return when (rules) {
        LegeSuspensjonRules.BEHANDLER_SUSPENDERT -> behandlerSuspendert
    }
}
