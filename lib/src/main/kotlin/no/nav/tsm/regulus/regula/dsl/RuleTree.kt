package no.nav.tsm.regulus.regula.dsl

internal enum class RuleStatus {
    OK,
    MANUAL_PROCESSING,
    INVALID,
}

internal interface RuleOutcome {
    val status: RuleStatus
    val rule: String
    val messageForUser: String
    val messageForSender: String
}

internal sealed class TreeNode<Enum> {

    internal sealed class LeafNode<Enum>(val status: RuleStatus, val juridisk: RuleJuridisk) :
        TreeNode<Enum>() {
        internal class OK<Enum> internal constructor(juridisk: RuleJuridisk) :
            LeafNode<Enum>(RuleStatus.OK, juridisk)

        internal class MANUAL<Enum>
        internal constructor(val outcome: RuleOutcome, juridisk: RuleJuridisk) :
            LeafNode<Enum>(RuleStatus.MANUAL_PROCESSING, juridisk)

        internal class INVALID<Enum>
        internal constructor(val outcome: RuleOutcome, juridisk: RuleJuridisk) :
            LeafNode<Enum>(RuleStatus.INVALID, juridisk)

        override fun toString(): String {
            val ruleOutcome =
                when (this) {
                    is OK -> null
                    is MANUAL -> outcome
                    is INVALID -> outcome
                }

            return status.name + (ruleOutcome?.let { "->${ruleOutcome.rule}" } ?: "")
        }
    }

    internal class RuleNode<Enum> internal constructor(val rule: Enum) : TreeNode<Enum>() {
        lateinit var yes: TreeNode<Enum>
        lateinit var no: TreeNode<Enum>

        internal fun yes(rule: Enum, init: RuleNode<Enum>.() -> Unit) {
            yes = RuleNode(rule).apply(init)
        }

        internal fun no(rule: Enum, init: RuleNode<Enum>.() -> Unit) {
            no = RuleNode(rule).apply(init)
        }

        internal fun yes(result: LeafNode<Enum>) {
            yes = result
        }

        internal fun no(result: LeafNode<Enum>) {
            no = result
        }
    }
}

internal fun <Enum> tree(
    rule: Enum,
    init: TreeNode.RuleNode<Enum>.() -> Unit,
): TreeNode.RuleNode<Enum> = TreeNode.RuleNode(rule).apply(init)

internal fun <Enum> rule(
    rule: Enum,
    init: TreeNode.RuleNode<Enum>.() -> Unit,
): TreeNode.RuleNode<Enum> = TreeNode.RuleNode(rule).apply(init)
