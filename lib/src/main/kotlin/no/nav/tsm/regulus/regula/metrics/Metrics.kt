package no.nav.tsm.regulus.regula.metrics

import no.nav.tsm.regulus.regula.RegulaResult

private val appName = System.getenv("NAIS_APP_NAME") ?: "unknown"

internal fun registerVersionMetrics() {
    libVersionMetric.labels(appName).set(RegulaVersion.VERSION.toDouble())
}

internal fun registerResultMetrics(regulaResult: RegulaResult) {
    ruleNodeRuleHitCounter
        .labels(regulaResult.status.name, regulaResult.ruleHits.firstOrNull()?.rule ?: "OK")
        .inc()

    regulaResult.results.forEach { ruleNodeRulePathCounter.labels(it.rulePath).inc() }
}
