package no.nav.tsm.regulus.regula.executor

data class RuleExecutionResult(val status: RuleStatus, val ruleHits: List<RuleInfo>)

data class RuleInfo(
    val ruleName: String,
    val messageForSender: String,
    val messageForUser: String,
    val ruleStatus: RuleStatus
)
