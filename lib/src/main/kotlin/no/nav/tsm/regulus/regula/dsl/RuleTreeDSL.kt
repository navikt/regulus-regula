package no.nav.tsm.regulus.regula.dsl

sealed class TreeNode<Enum, Result>

class ResultNode<Enum, Result>(val result: Result) : TreeNode<Enum, Result>()

class RuleNode<Enum, Result> internal constructor(val rule: Enum) : TreeNode<Enum, Result>() {
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

fun <Enum, Result> tree(rule: Enum, init: RuleNode<Enum, Result>.() -> Unit): RuleNode<Enum, Result> =
    RuleNode<Enum, Result>(rule).apply(init)

fun <Enum, Result> rule(rule: Enum, init: RuleNode<Enum, Result>.() -> Unit): RuleNode<Enum, Result> =
    RuleNode<Enum, Result>(rule).apply(init)
