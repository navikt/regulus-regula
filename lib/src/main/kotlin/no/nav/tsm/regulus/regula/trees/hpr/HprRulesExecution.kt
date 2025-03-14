package no.nav.tsm.regulus.regula.trees.hpr

import no.nav.tsm.regulus.regula.dsl.*
import no.nav.tsm.regulus.regula.executor.RuleExecution
import no.nav.tsm.regulus.regula.executor.RuleResult
import org.slf4j.LoggerFactory

typealias HPRTreeNode = TreeNode<HprRule, RuleResult>

typealias HPRTreeOutput = TreeOutput<HprRule, RuleResult>

class HprRulesExecution(private val hprRulePayload: HprRulePayload) : RuleExecution<HprRule> {
    private val logger = LoggerFactory.getLogger(this::class.java)

    override fun runRules(): Pair<HPRTreeOutput, Juridisk> =
        hprRuleTree.first.evaluate(hprRulePayload).also { hprRulePath ->
            logger.info("Rules ${hprRulePayload.sykmeldingId}, ${hprRulePath.printRulePath()}")
        } to hprRuleTree.second
}

private fun HPRTreeNode.evaluate(payload: HprRulePayload): HPRTreeOutput =
    when (this) {
        is ResultNode -> HPRTreeOutput(treeResult = result)
        is RuleNode -> {
            val rule = getRule(rule)
            val result = rule(payload)
            val childNode = if (result.ruleResult) yes else no
            result join childNode.evaluate(payload)
        }
    }
