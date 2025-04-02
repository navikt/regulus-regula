package no.nav.tsm.regulus.regula.metrics

import no.nav.tsm.regulus.regula.RegulaResult

internal fun registerVersionMetrics() {
    libVersionMetric.set(RegulaVersion.VERSION.toDouble())
}

internal fun registerResultMetrics(regulaResult: RegulaResult) {
    ruleNodeRuleHitCounter
        .labels(regulaResult.status.name, regulaResult.outcome?.rule ?: "OK")
        .inc()

    regulaResult.results.forEach { ruleNodeRulePathCounter.labels(it.rulePath).inc() }
}
