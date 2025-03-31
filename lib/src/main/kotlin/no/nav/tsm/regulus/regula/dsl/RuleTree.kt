package no.nav.tsm.regulus.regula.dsl

internal sealed class TreeNode<Enum>

internal class ResultNode<Enum>(val result: RuleResult) : TreeNode<Enum>()

internal class RuleNode<Enum> internal constructor(val rule: Enum) : TreeNode<Enum>() {
    lateinit var yes: TreeNode<Enum>
    lateinit var no: TreeNode<Enum>

    internal fun yes(rule: Enum, init: RuleNode<Enum>.() -> Unit) {
        yes = RuleNode(rule).apply(init)
    }

    internal fun no(rule: Enum, init: RuleNode<Enum>.() -> Unit) {
        no = RuleNode(rule).apply(init)
    }

    internal fun yes(result: RuleResult) {
        yes = ResultNode(result)
    }

    internal fun no(result: RuleResult) {
        no = ResultNode(result)
    }
}

internal fun <Enum> tree(rule: Enum, init: RuleNode<Enum>.() -> Unit): RuleNode<Enum> =
    RuleNode(rule).apply(init)

internal fun <Enum> rule(rule: Enum, init: RuleNode<Enum>.() -> Unit): RuleNode<Enum> =
    RuleNode(rule).apply(init)
