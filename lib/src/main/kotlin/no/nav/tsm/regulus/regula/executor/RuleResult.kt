package no.nav.tsm.regulus.regula.executor

import no.nav.tsm.regulus.regula.dsl.RuleNode

internal enum class RuleStatus {
    OK,
    MANUAL_PROCESSING,
    INVALID,
}

internal interface RuleOutcome {
    val status: RuleStatus
    val rule: String
    val messageForUser: String
    val messageForSender: String
}

internal data class RuleResult(val status: RuleStatus, val ruleOutcome: RuleOutcome?) {
    override fun toString(): String =
        status.name + (ruleOutcome?.let { "->${ruleOutcome.rule}" } ?: "")
}

internal fun <T> RuleNode<T, RuleResult>.yes(status: RuleStatus) = yes(RuleResult(status, null))

internal fun <T> RuleNode<T, RuleResult>.yes(status: RuleStatus, ruleOutcome: RuleOutcome) =
    yes(RuleResult(status, ruleOutcome))

internal fun <T> RuleNode<T, RuleResult>.no(status: RuleStatus) = no(RuleResult(status, null))

internal fun <T> RuleNode<T, RuleResult>.no(status: RuleStatus, ruleOutcome: RuleOutcome) =
    no(RuleResult(status, ruleOutcome))
