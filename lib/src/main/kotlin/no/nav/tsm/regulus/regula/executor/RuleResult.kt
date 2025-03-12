package no.nav.tsm.regulus.regula.executor

data class RuleHit(
    val status: RuleStatus,
    val rule: String,
    val messageForUser: String,
    val messageForSender: String,
)

data class RuleResult(
    val status: RuleStatus,
    val ruleHit: RuleHit?,
) {
    override fun toString(): String {
        return status.name + (ruleHit?.let { "->${ruleHit.rule}" } ?: "")
    }
}
