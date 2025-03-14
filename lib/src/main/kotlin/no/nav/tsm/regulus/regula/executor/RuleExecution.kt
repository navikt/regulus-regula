package no.nav.tsm.regulus.regula.executor

import no.nav.tsm.regulus.regula.dsl.Juridisk
import no.nav.tsm.regulus.regula.dsl.TreeOutput

interface RuleExecution<Rules> {
    fun runRules(): Pair<TreeOutput<Rules, RuleResult>, Juridisk>
}

data class RuleExecutionResult(val status: RuleStatus, val ruleHits: List<RuleInfo>)

data class RuleInfo(
    val ruleName: String,
    val messageForSender: String,
    val messageForUser: String,
    val ruleStatus: RuleStatus,
)
