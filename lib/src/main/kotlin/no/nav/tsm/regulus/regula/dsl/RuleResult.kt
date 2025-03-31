package no.nav.tsm.regulus.regula.dsl

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

/**
 * The result of a single rule. A combination of a status and an optional outcome. Only leaf nodes
 * should have outcomes.
 */
internal data class RuleResult(val status: RuleStatus, val ruleOutcome: RuleOutcome?) {
    override fun toString(): String =
        status.name + (ruleOutcome?.let { "->${ruleOutcome.rule}" } ?: "")
}

internal fun <T> RuleNode<T>.yes(status: RuleStatus) = yes(RuleResult(status, null))

internal fun <T> RuleNode<T>.yes(status: RuleStatus, ruleOutcome: RuleOutcome) =
    yes(RuleResult(status, ruleOutcome))

internal fun <T> RuleNode<T>.no(status: RuleStatus) = no(RuleResult(status, null))

internal fun <T> RuleNode<T>.no(status: RuleStatus, ruleOutcome: RuleOutcome) =
    no(RuleResult(status, ruleOutcome))
