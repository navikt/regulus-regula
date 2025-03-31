package no.nav.tsm.regulus.regula.executor

import no.nav.tsm.regulus.regula.dsl.RuleStatus
import no.nav.tsm.regulus.regula.dsl.TreeOutput

internal typealias RuleExecutionResult = List<TreeOutput<*>>

internal fun runRules(
    sequence: Sequence<TreeExecutor<*, *>>,
    mode: ExecutionMode,
): RuleExecutionResult {
    val results = mutableListOf<TreeOutput<*>>()

    for (seq in sequence) {
        val result = seq.execute(mode)
        results.add(result)
        if (result.treeResult.status != RuleStatus.OK) {
            break
        }
    }

    return results
}
