package no.nav.tsm.regulus.regula.executor

import no.nav.tsm.regulus.regula.dsl.TreeOutput
import no.nav.tsm.regulus.regula.juridisk.Juridisk

internal typealias RuleExecutionResult = List<Pair<TreeOutput<*, RuleResult>, Juridisk>>

internal fun runRules(
    sequence: Sequence<TreeExecutor<*, *>>,
    mode: ExecutionMode,
): RuleExecutionResult {
    val results = mutableListOf<Pair<TreeOutput<*, RuleResult>, Juridisk>>()

    for (seq in sequence) {
        val result = seq.execute(mode)
        results.add(result)
        if (result.first.treeResult.status != RuleStatus.OK) {
            break
        }
    }

    return results
}
