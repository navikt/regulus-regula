package no.nav.tsm.regulus.regula.metrics

private val appName = System.getenv("NAIS_APP_NAME") ?: "unknown"

internal fun registerVersionMetrics() {
    libVersionMetric.labels(appName).set(RegulaVersion.VERSION.toDouble())
}
