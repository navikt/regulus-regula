package no.nav.tsm.regulus.regula.metrics

private val clientId = System.getenv("NAIS_CLIENT_ID") ?: "unknown"

internal fun registerVersionMetrics() {
    libVersionMetric.labels(clientId).set(RegulaVersion.VERSION.toDouble())
}
