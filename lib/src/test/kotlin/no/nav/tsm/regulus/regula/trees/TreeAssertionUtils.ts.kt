package no.nav.tsm.regulus.regula.trees

import kotlin.test.assertEquals
import no.nav.tsm.regulus.regula.dsl.RuleOutput

fun <T> assertPath(result: List<RuleOutput<T>>, expectedPath: List<Pair<T, Boolean>>) {
    val pathMap = result.map { it.rule to it.ruleResult }

    assertEquals(pathMap, expectedPath)
}
