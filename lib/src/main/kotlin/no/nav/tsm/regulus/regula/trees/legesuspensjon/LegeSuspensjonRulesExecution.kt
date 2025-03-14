package no.nav.tsm.regulus.regula.trees.legesuspensjon

import no.nav.tsm.regulus.regula.dsl.*
import no.nav.tsm.regulus.regula.executor.RuleExecution
import no.nav.tsm.regulus.regula.executor.RuleResult
import org.slf4j.LoggerFactory

typealias LegeSuspensjonTreeOutput = TreeOutput<LegeSuspensjonRule, RuleResult>

typealias LegeSuspensjonTreeNode = TreeNode<LegeSuspensjonRule, RuleResult>

class LegeSuspensjonRulesExecution(
    private val sykmeldingId: String,
    private val behandlerSuspendert: Boolean,
) : RuleExecution<LegeSuspensjonRule> {
    private val logger = LoggerFactory.getLogger(this::class.java)

    override fun runRules(): Pair<LegeSuspensjonTreeOutput, Juridisk> =
        legeSuspensjonRuleTree.first.evaluate(behandlerSuspendert).also { legeSuspensjonRulePath ->
            logger.info("Rules ${sykmeldingId}, ${legeSuspensjonRulePath.printRulePath()}")
        } to legeSuspensjonRuleTree.second
}

private fun LegeSuspensjonTreeNode.evaluate(
    behandlerSuspendert: Boolean
): LegeSuspensjonTreeOutput =
    when (this) {
        is ResultNode -> LegeSuspensjonTreeOutput(treeResult = result)
        is RuleNode -> {
            val rule = getRule(rule)
            val result = rule(behandlerSuspendert)
            val childNode = if (result.ruleResult) yes else no
            result join childNode.evaluate(behandlerSuspendert)
        }
    }
