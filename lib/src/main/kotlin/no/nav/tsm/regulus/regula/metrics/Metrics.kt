package no.nav.tsm.regulus.regula.metrics

import no.nav.tsm.regulus.regula.RegulaResult
import no.nav.tsm.regulus.regula.executor.ExecutionMode

internal fun registerVersionMetrics() {
    libVersionMetric.set(RegulaVersion.VERSION.toDouble())
}

internal fun registerResultMetrics(regulaResult: RegulaResult, mode: ExecutionMode) {
    val labelValue =
        when (regulaResult) {
            is RegulaResult.OK -> "OK"
            is RegulaResult.NotOk -> regulaResult.outcome.rule
        }

    ruleNodeRuleHitCounter.labels(regulaResult.status.name, labelValue, mode.name).inc()

    regulaResult.results.forEach { ruleNodeRulePathCounter.labels(it.rulePath, mode.name).inc() }
}
