package no.nav.tsm.regulus.regula.rules.trees

import kotlin.test.assertEquals
import no.nav.tsm.regulus.regula.dsl.RuleOutput
import no.nav.tsm.regulus.regula.dsl.TreeOutput

internal fun <T> assertPath(result: List<RuleOutput<T>>, expectedPath: List<Pair<T, Boolean>>) {
    val pathMap = result.map { it.rule to it.ruleResult }

    assertEquals(pathMap, expectedPath)
}

internal fun <Enum> TreeOutput<Enum>.debugPath(): TreeOutput<Enum> {
    println("[DEBUG] Tree was ${this.treeResult}, path:")
    println(
        this.rulePath.joinToString(separator = "\n") {
            "→ ${it.rule}(${if (it.ruleResult) "yes" else "no"}) \n\t ${it.ruleInputs.map { (k, v) -> "$k: $v" }}"
        }
    )

    return this
}

internal fun <Enum> TreeOutput<Enum>.debugPath(
    expectedPath: List<Pair<Enum, Boolean>>
): TreeOutput<Enum> {
    println("[DEBUG] Tree was ${this.treeResult}, path:")

    var ruleIndex = -1
    println(
        this.rulePath.joinToString(separator = "\n") {
            ruleIndex++

            if (ruleIndex >= expectedPath.size) {
                "→ ${it.rule.toString().red()}(${(if (it.ruleResult) "got ${"nothing".red()}, expected ${"no".yellow()}" else "got ${"nothing".red()}, expected ${"yes".yellow()}")}) \n\t ${it.ruleInputs.map { (k, v) -> "$k: $v" }}"
            } else if (it.ruleResult == expectedPath[ruleIndex].second) {
                "→ ${it.rule}(${(if (it.ruleResult) "yes" else "no").green()}) \n\t ${it.ruleInputs.map { (k, v) -> "$k: $v" }}"
            } else {
                "→ ${it.rule.toString().red()}(${(if (it.ruleResult) "got ${"yes".red()}, expected ${"no".yellow()}" else "got ${"no".red()}, expected ${"yes".yellow()}")}) \n\t ${it.ruleInputs.map { (k, v) -> "$k: $v" }}"
            }
        }
    )

    return this
}

private fun String.red() = "\u001B[31m$this\u001B[0m"

private fun String.green() = "\u001B[32m$this\u001B[0m"

private fun String.yellow() = "\u001B[33m$this\u001B[0m"
