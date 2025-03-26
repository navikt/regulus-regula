package no.nav.tsm.regulus.regula.dsl

import no.nav.tsm.regulus.regula.executor.RuleResult

internal sealed class TreeNode<Enum, Result>

internal class ResultNode<Enum, Result>(val result: Result) : TreeNode<Enum, Result>()

internal class RuleNode<Enum, Result> internal constructor(val rule: Enum) :
    TreeNode<Enum, Result>() {
    lateinit var yes: TreeNode<Enum, Result>
    lateinit var no: TreeNode<Enum, Result>

    internal fun yes(rule: Enum, init: RuleNode<Enum, Result>.() -> Unit) {
        yes = RuleNode<Enum, Result>(rule).apply(init)
    }

    internal fun no(rule: Enum, init: RuleNode<Enum, Result>.() -> Unit) {
        no = RuleNode<Enum, Result>(rule).apply(init)
    }

    internal fun yes(result: Result) {
        yes = ResultNode(result)
    }

    internal fun no(result: Result) {
        no = ResultNode(result)
    }
}

internal fun <Enum> tree(
    rule: Enum,
    init: RuleNode<Enum, RuleResult>.() -> Unit,
): RuleNode<Enum, RuleResult> = RuleNode<Enum, RuleResult>(rule).apply(init)

internal fun <Enum> rule(
    rule: Enum,
    init: RuleNode<Enum, RuleResult>.() -> Unit,
): RuleNode<Enum, RuleResult> = RuleNode<Enum, RuleResult>(rule).apply(init)
