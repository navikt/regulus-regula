package no.nav.tsm.regulus.regula.dsl

import no.nav.tsm.regulus.regula.dsl.TreeNode.*
import no.nav.tsm.regulus.regula.dsl.TreeNode.LeafNode.*

/** The output of a single rule. */
internal data class RuleOutput<Enum>(
    /** All values that have been used to perform the functional check of the rule. */
    val ruleInputs: Map<String, Any> = emptyMap(),
    /**
     * Did the rule true or false according to the functional name of the test? Not a pass/fail
     * boolean.
     */
    val ruleResult: Boolean,
    /** Name (enum) of the rule. */
    val rule: Enum,
)

/**
 * The cumulative output of a tree. This is the result of the tree traversal, and contains all the
 * inputs that have been used to perform the functional checks of the rules in the tree.
 */
internal data class TreeOutput<Enum>(
    /**
     * Cumulative inputs that have been used to perform the functional checks of the rules in the
     * tree.
     */
    val ruleInputs: Map<String, Any> = mapOf(),
    /** The path of rules that have been traversed in the tree. */
    val rulePath: List<RuleOutput<Enum>> = emptyList(),
    /** The final result of the tree traversal. */
    val treeResult: LeafNode<Enum>,
)

internal fun <Enum> TreeOutput<Enum>.getRulePath(): String =
    rulePath
        .joinToString(separator = "->") { "${it.rule}(${if (it.ruleResult) "yes" else "no"})" }
        .plus("->$treeResult")

internal fun <Enum> LeafNode<Enum>.getOutcome(): RuleOutcome? =
    when (this) {
        is INVALID -> outcome
        is MANUAL -> outcome
        is OK -> null
    }
