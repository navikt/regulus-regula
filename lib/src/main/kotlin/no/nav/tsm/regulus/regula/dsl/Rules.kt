package no.nav.tsm.regulus.regula.dsl

data class RuleOutput<Enum>(
    val ruleInputs: Map<String, Any> = emptyMap(),
    val ruleResult: Boolean,
    val rule: Enum,
)

data class TreeOutput<Enum, Result>(
    val ruleInputs: Map<String, Any> = mapOf(),
    val rulePath: List<RuleOutput<Enum>> = emptyList(),
    val treeResult: Result,
)

fun <Enum, Result> TreeOutput<Enum, Result>.printRulePath(): String =
    rulePath
        .joinToString(separator = "->") { "${it.rule}(${if (it.ruleResult) "yes" else "no"})" }
        .plus("->$treeResult")

infix fun <Enum, Result> RuleOutput<Enum>.join(rulesOutput: TreeOutput<Enum, Result>) =
    TreeOutput(
        ruleInputs = ruleInputs + rulesOutput.ruleInputs,
        rulePath = listOf(this) + rulesOutput.rulePath,
        treeResult = rulesOutput.treeResult,
    )
