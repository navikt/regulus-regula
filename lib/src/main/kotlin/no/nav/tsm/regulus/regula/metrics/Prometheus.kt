package no.nav.tsm.regulus.regula.metrics

import io.prometheus.client.Counter
import io.prometheus.client.Gauge

private val namespace = "regulus_regula"

internal val libVersionMetric: Gauge =
    Gauge.build()
        .namespace(namespace)
        .name("library_version")
        .help("Current version of the library")
        .register()

val ruleNodeRuleHitCounter: Counter =
    Counter.Builder()
        .namespace(namespace)
        .name("rules_rule_hit_counter_total")
        .labelNames("status", "rule_hit", "mode")
        .help("Counts rulenode rules")
        .register()

val ruleNodeRulePathCounter: Counter =
    Counter.Builder()
        .namespace(namespace)
        .name("rules_rule_path_counter_total")
        .labelNames("path", "mode")
        .help("Counts rule tree rule paths")
        .register()
