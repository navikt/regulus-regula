package no.nav.tsm.regulus.regula.metrics

import io.prometheus.metrics.core.metrics.Counter
import io.prometheus.metrics.core.metrics.Gauge

private val namespace = "regulus_regula"

internal val libVersionMetric: Gauge =
    Gauge.builder()
        .name("${namespace}_library_version")
        .help("Current version of the library")
        .register()

val ruleNodeRuleHitCounter: Counter =
    Counter.builder()
        .name("${namespace}_rules_rule_hit_counter_total")
        .labelNames("status", "rule_hit", "mode")
        .help("Counts rulenode rules")
        .register()

val ruleNodeRulePathCounter: Counter =
    Counter.builder()
        .name("${namespace}_rules_rule_path_counter_total")
        .labelNames("path", "mode")
        .help("Counts rule tree rule paths")
        .register()
