package no.nav.tsm.regulus.regula.trees.validation

import no.nav.tsm.regulus.regula.dsl.*
import no.nav.tsm.regulus.regula.executor.RuleExecution
import no.nav.tsm.regulus.regula.executor.RuleResult
import org.slf4j.LoggerFactory

typealias ValidationTreeNode = TreeNode<ValidationRule, RuleResult>

typealias ValidationTreeOutput = TreeOutput<ValidationRule, RuleResult>

class ValidationRulesExecution(
    private val sykmeldingId: String,
    private val validationRulePayload: ValidationRulePayload,
) : RuleExecution<ValidationRule> {
    private val logger = LoggerFactory.getLogger(this::class.java)

    override fun runRules(): Pair<ValidationTreeOutput, Juridisk> =
        validationRuleTree.first.evaluate(validationRulePayload).also { validationRulePath ->
            logger.info("Rules ${sykmeldingId}, ${validationRulePath.printRulePath()}")
        } to validationRuleTree.second
}

private fun ValidationTreeNode.evaluate(
    validationRulePayload: ValidationRulePayload
): ValidationTreeOutput =
    when (this) {
        is ResultNode -> ValidationTreeOutput(treeResult = result)
        is RuleNode -> {
            val rule = getRule(rule)
            val result = rule(validationRulePayload)
            val childNode = if (result.ruleResult) yes else no
            result join childNode.evaluate(validationRulePayload)
        }
    }
