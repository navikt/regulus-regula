package no.nav.tsm.regulus.regula.executor

import no.nav.tsm.regulus.regula.dsl.RuleOutput
import no.nav.tsm.regulus.regula.dsl.RuleStatus
import no.nav.tsm.regulus.regula.dsl.TreeNode
import no.nav.tsm.regulus.regula.dsl.TreeNode.*
import no.nav.tsm.regulus.regula.dsl.TreeNode.LeafNode.*
import no.nav.tsm.regulus.regula.dsl.TreeOutput
import no.nav.tsm.regulus.regula.dsl.getRulePath
import org.slf4j.LoggerFactory

enum class ExecutionMode {
    /** Normal mode will execute as normal */
    NORMAL,

    /** Paper mode will change any invalid rules to manual processing */
    PAPIR,
}

/**
 * This executor binds together:
 * - a tree (generic definiton using DSL)
 * - a set of rule implementations
 * - a payload specific to the rules/tree
 */
internal abstract class TreeExecutor<RuleEnum, Payload : BasePayload>(
    private val name: String,
    private val tree: RuleNode<RuleEnum>,
    private val payload: Payload,
) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    abstract fun getRule(rule: RuleEnum): (Payload) -> RuleOutput<RuleEnum>

    fun execute(mode: ExecutionMode): TreeOutput<RuleEnum> {
        val executedTreeResult =
            tree.evaluate(payload).also { treeOutput: TreeOutput<RuleEnum> ->
                logger.info(
                    "Rules (mode=${mode.name}) ${payload.sykmeldingId}, ${treeOutput.getRulePath()}"
                )
            }

        return if (mode == ExecutionMode.PAPIR) {
            executedTreeResult.flipInvalidToManuell()
        } else {
            executedTreeResult
        }
    }

    private fun TreeOutput<RuleEnum>.flipInvalidToManuell(): TreeOutput<RuleEnum> {
        if (treeResult.status != RuleStatus.INVALID) {
            return this
        }

        val flippedResult =
            when (treeResult) {
                is OK<*> -> return this
                is MANUAL<*> -> return this
                is INVALID<*> -> MANUAL<RuleEnum>(this.treeResult.outcome, this.treeResult.juridisk)
            }

        return copy(treeResult = flippedResult)
    }

    private fun TreeNode<RuleEnum>.evaluate(payload: Payload): TreeOutput<RuleEnum> =
        when (this) {
            is LeafNode -> TreeOutput(name = name, treeResult = this)
            is RuleNode -> {
                val rule = getRule(rule)
                val result = rule(payload)
                val childNode = if (result.ruleResult) yes else no
                result join childNode.evaluate(payload)
            }
        }
}

private infix fun <Enum> RuleOutput<Enum>.join(rulesOutput: TreeOutput<Enum>) =
    TreeOutput(
        name = rulesOutput.name,
        ruleInputs = ruleInputs + rulesOutput.ruleInputs,
        rulePath = listOf(this) + rulesOutput.rulePath,
        treeResult = rulesOutput.treeResult,
    )
