package no.nav.tsm.regulus.regula.dsl

internal data class RuleOutput<Enum>(
    val ruleInputs: Map<String, Any> = emptyMap(),
    val ruleResult: Boolean,
    val rule: Enum,
)

internal data class TreeOutput<Enum, Result>(
    val ruleInputs: Map<String, Any> = mapOf(),
    val rulePath: List<RuleOutput<Enum>> = emptyList(),
    val treeResult: Result,
)

internal fun <Enum, Result> TreeOutput<Enum, Result>.printRulePath(): String =
    rulePath
        .joinToString(separator = "->") { "${it.rule}(${if (it.ruleResult) "yes" else "no"})" }
        .plus("->$treeResult")

internal infix fun <Enum, Result> RuleOutput<Enum>.join(rulesOutput: TreeOutput<Enum, Result>) =
    TreeOutput(
        ruleInputs = ruleInputs + rulesOutput.ruleInputs,
        rulePath = listOf(this) + rulesOutput.rulePath,
        treeResult = rulesOutput.treeResult,
    )
