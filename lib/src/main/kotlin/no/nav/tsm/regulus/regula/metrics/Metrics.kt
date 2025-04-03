package no.nav.tsm.regulus.regula.metrics

import no.nav.tsm.regulus.regula.RegulaResult
import no.nav.tsm.regulus.regula.executor.ExecutionMode

internal fun registerVersionMetrics() {
    libVersionMetric.set(RegulaVersion.VERSION.toDouble())
}

internal fun registerResultMetrics(regulaResult: RegulaResult, mode: ExecutionMode) {
    ruleNodeRuleHitCounter
        .labels(regulaResult.status.name, regulaResult.outcome?.rule ?: "OK", mode.name)
        .inc()

    regulaResult.results.forEach { ruleNodeRulePathCounter.labels(it.rulePath, mode.name).inc() }
}
