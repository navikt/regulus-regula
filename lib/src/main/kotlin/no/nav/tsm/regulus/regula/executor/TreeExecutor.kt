package no.nav.tsm.regulus.regula.executor

import no.nav.tsm.regulus.regula.dsl.ResultNode
import no.nav.tsm.regulus.regula.dsl.RuleNode
import no.nav.tsm.regulus.regula.dsl.RuleOutput
import no.nav.tsm.regulus.regula.dsl.RuleStatus
import no.nav.tsm.regulus.regula.dsl.TreeNode
import no.nav.tsm.regulus.regula.dsl.TreeOutput
import no.nav.tsm.regulus.regula.dsl.getRulePath
import no.nav.tsm.regulus.regula.juridisk.Juridisk
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
    private val tree: Pair<RuleNode<RuleEnum>, Juridisk>,
    private val payload: Payload,
) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    abstract fun getRule(rule: RuleEnum): (Payload) -> RuleOutput<RuleEnum>

    fun execute(mode: ExecutionMode): Pair<TreeOutput<RuleEnum>, Juridisk> {
        val executedTreeResult =
            tree.first.evaluate(payload).also { treeOutput: TreeOutput<RuleEnum> ->
                logger.info(
                    "Rules (mode=${mode.name}) ${payload.sykmeldingId}, ${treeOutput.getRulePath()}"
                )
            }

        return if (mode == ExecutionMode.PAPIR) {
            executedTreeResult.flipInvalidToManuell() to tree.second
        } else {
            executedTreeResult to tree.second
        }
    }

    private fun TreeOutput<RuleEnum>.flipInvalidToManuell(): TreeOutput<RuleEnum> {
        if (treeResult.status != RuleStatus.INVALID) {
            return this
        }

        return copy(treeResult = treeResult.copy(status = RuleStatus.MANUAL_PROCESSING))
    }

    private fun TreeNode<RuleEnum>.evaluate(payload: Payload): TreeOutput<RuleEnum> =
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

private infix fun <Enum> RuleOutput<Enum>.join(rulesOutput: TreeOutput<Enum>) =
    TreeOutput(
        ruleInputs = ruleInputs + rulesOutput.ruleInputs,
        rulePath = listOf(this) + rulesOutput.rulePath,
        treeResult = rulesOutput.treeResult,
    )
