package no.nav.tsm.regulus.regula.rules.trees.legeSuspensjon

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import no.nav.tsm.regulus.regula.dsl.RuleStatus
import no.nav.tsm.regulus.regula.dsl.getOutcome
import no.nav.tsm.regulus.regula.executor.ExecutionMode
import no.nav.tsm.regulus.regula.rules.trees.assertPath

class LegeSuspensjonRulesTest {
    @Test
    fun `Er ikkje suspendert, Status OK`() {
        val (result) =
            LegeSuspensjonRules(
                    LegeSuspensjonRulePayload(sykmeldingId = "foo-bar", behandlerSuspendert = false)
                )
                .execute(ExecutionMode.NORMAL)

        assertEquals(result.treeResult.status, RuleStatus.OK)
        assertNull(result.treeResult.getOutcome())

        assertPath(result.rulePath, listOf(LegeSuspensjonRule.BEHANDLER_SUSPENDERT to false))
        assertEquals(result.ruleInputs, mapOf("suspendert" to false))
    }

    @Test
    fun `Er suspendert, Status INVALID`() {
        val (result) =
            LegeSuspensjonRules(
                    LegeSuspensjonRulePayload(sykmeldingId = "foo-bar", behandlerSuspendert = true)
                )
                .execute(ExecutionMode.NORMAL)

        assertEquals(result.treeResult.status, RuleStatus.INVALID)
        assertEquals(
            result.treeResult.getOutcome(),
            LegeSuspensjonRule.Outcomes.BEHANDLER_SUSPENDERT,
        )

        assertPath(result.rulePath, listOf(LegeSuspensjonRule.BEHANDLER_SUSPENDERT to true))
        assertEquals(result.ruleInputs, mapOf("suspendert" to true))
    }
}
