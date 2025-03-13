package no.nav.tsm.regulus.regula.trees.legesuspensjon

import no.nav.tsm.regulus.regula.executor.RuleStatus
import org.junit.jupiter.api.Assertions.*
import kotlin.test.Test

class LegeSuspensjonRulesExecutionTest {
    @Test
    fun `Er ikkje suspendert, Status OK`() {
        val result = LegeSuspensjonRulesExecution("foo-bar", false).runRules()

        assertEquals(result.first.treeResult.status, RuleStatus.OK)
        assertNull(result.first.treeResult.ruleOutcome)

        assertEquals(
            result.first.rulePath.map { it.rule to it.ruleResult },
            listOf(
                LegeSuspensjonRule.BEHANDLER_SUSPENDERT to false,
            )
        )
        assertEquals(
            result.first.ruleInputs,
            mapOf("suspendert" to false)
        )

    }

    @Test
    fun `Er suspendert, Status INVALID`() {
        val result = LegeSuspensjonRulesExecution("foo-bar", true).runRules()

        assertEquals(result.first.treeResult.status, RuleStatus.INVALID)
        assertEquals(result.first.treeResult.ruleOutcome, LegeSuspensjonRule.Outcomes.BEHANDLER_SUSPENDERT)

        assertEquals(
            result.first.rulePath.map { it.rule to it.ruleResult },
            listOf(
                LegeSuspensjonRule.BEHANDLER_SUSPENDERT to true,
            )
        )
        assertEquals(
            result.first.ruleInputs,
            mapOf("suspendert" to true)
        )

    }
}