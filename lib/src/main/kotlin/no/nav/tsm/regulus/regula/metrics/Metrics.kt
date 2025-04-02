package no.nav.tsm.regulus.regula.metrics

import org.slf4j.LoggerFactory

private val clientId = System.getenv("NAIS_CLIENT_ID") ?: "unknown"

private val logger = LoggerFactory.getLogger("regula.metrics")

internal fun registerVersionMetrics() {
    logger.info(
        "Registering version metrics for clientId: $clientId, version: ${RegulaVersion.VERSION}"
    )

    libVersionMetric.labels(clientId).set(RegulaVersion.VERSION.toDouble())
}
