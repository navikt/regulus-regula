package no.nav.tsm.regulus.regula.trees

import kotlin.test.assertEquals
import no.nav.tsm.regulus.regula.dsl.RuleOutput
import no.nav.tsm.regulus.regula.dsl.TreeOutput

fun <T> assertPath(result: List<RuleOutput<T>>, expectedPath: List<Pair<T, Boolean>>) {
    val pathMap = result.map { it.rule to it.ruleResult }

    assertEquals(pathMap, expectedPath)
}

fun <T, S> TreeOutput<T, S>.debugPath() {
    println("[DEBUG] Tree was ${this.treeResult}, path:")
    println(
        this.rulePath.joinToString(separator = "\n") {
            "→ ${it.rule}(${if (it.ruleResult) "yes" else "no"}) \n\t ${it.ruleInputs.map { (k, v) -> "$k: $v" }}"
        }
    )
}
