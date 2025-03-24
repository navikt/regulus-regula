package no.nav.tsm.regulus.regula.executor

import no.nav.tsm.regulus.regula.dsl.TreeOutput

typealias RuleExecutionResult = List<Pair<TreeOutput<*, RuleResult>, Juridisk>>

fun runRules(sequence: Sequence<TreeExecutor<*, *>>): RuleExecutionResult {
    val results = mutableListOf<Pair<TreeOutput<*, RuleResult>, Juridisk>>()

    for (seq in sequence) {
        val result = seq.execute()
        results.add(result)
        if (result.first.treeResult.status != RuleStatus.OK) {
            break
        }
    }

    return results
}
