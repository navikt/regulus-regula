package no.nav.tsm.regulus.regula.executor

import no.nav.tsm.regulus.regula.dsl.ResultNode
import no.nav.tsm.regulus.regula.dsl.RuleNode
import no.nav.tsm.regulus.regula.dsl.RuleOutput
import no.nav.tsm.regulus.regula.dsl.TreeNode
import no.nav.tsm.regulus.regula.dsl.TreeOutput
import no.nav.tsm.regulus.regula.dsl.join
import no.nav.tsm.regulus.regula.dsl.printRulePath
import no.nav.tsm.regulus.regula.payload.BasePayload
import org.slf4j.LoggerFactory

/**
 * This executor binds together:
 * - a tree (generic definiton using DSL)
 * - a set of rule implementations
 * - a payload specific to the rules/tree
 */
internal abstract class TreeExecutor<RuleEnum, Payload : BasePayload>(
    private val tree: Pair<RuleNode<RuleEnum, RuleResult>, Juridisk>,
    private val payload: Payload,
) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    abstract fun getRule(rule: RuleEnum): (Payload) -> RuleOutput<RuleEnum>

    fun execute(): Pair<TreeOutput<RuleEnum, RuleResult>, Juridisk> =
        tree.first.evaluate(payload).also { treeOutput: TreeOutput<RuleEnum, RuleResult> ->
            logger.info("Rules ${payload.sykmeldingId}, ${treeOutput.printRulePath()}")
        } to tree.second

    private fun TreeNode<RuleEnum, RuleResult>.evaluate(
        payload: Payload
    ): TreeOutput<RuleEnum, RuleResult> =
        when (this) {
            is ResultNode -> TreeOutput(treeResult = result)
            is RuleNode -> {
                val rule = getRule(rule)
                val result = rule(payload)
                val childNode = if (result.ruleResult) yes else no
                result join childNode.evaluate(payload)
            }
        }
}
