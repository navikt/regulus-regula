package no.nav.tsm.regulus.regula.metrics

import no.nav.tsm.regulus.regula.RegulaResult
import org.slf4j.LoggerFactory

private val version: Int =
    RegulaResult::class.java.`package`.implementationVersion?.let { it.toInt() } ?: -1

private val clientId = System.getenv("NAIS_CLIENT_ID") ?: "unknown"

private val logger = LoggerFactory.getLogger("regula.metrics")

internal fun registerVersionMetrics() {
    logger.info("Registering version metrics for clientId: $clientId, version: $version")

    libVersionMetric.labels(clientId).set(version.toDouble())
}
