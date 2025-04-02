package no.nav.tsm.regulus.regula.metrics

import io.prometheus.client.Gauge

internal val libVersionMetric: Gauge =
    Gauge.build()
        .namespace("regulus_regula")
        .name("library_version")
        .help("Current version of the library in apps")
        .labelNames("pod_name")
        .register()
