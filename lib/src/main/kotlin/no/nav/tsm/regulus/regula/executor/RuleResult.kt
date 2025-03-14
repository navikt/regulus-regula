package no.nav.tsm.regulus.regula.executor

import no.nav.tsm.regulus.regula.dsl.RuleNode

interface RuleOutcome {
    val status: RuleStatus
    val rule: String
    val messageForUser: String
    val messageForSender: String
}

data class RuleResult(val status: RuleStatus, val ruleOutcome: RuleOutcome?) {
    override fun toString(): String {
        return status.name + (ruleOutcome?.let { "->${ruleOutcome.rule}" } ?: "")
    }
}

fun <T> RuleNode<T, RuleResult>.yes(status: RuleStatus) = yes(RuleResult(status, null))

fun <T> RuleNode<T, RuleResult>.yes(status: RuleStatus, ruleOutcome: RuleOutcome) =
    yes(RuleResult(status, ruleOutcome))

fun <T> RuleNode<T, RuleResult>.no(status: RuleStatus) = no(RuleResult(status, null))

fun <T> RuleNode<T, RuleResult>.no(status: RuleStatus, ruleOutcome: RuleOutcome) =
    no(RuleResult(status, ruleOutcome))
