package no.nav.tsm.regulus.regula.trees.legesuspensjon

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import no.nav.tsm.regulus.regula.executor.RuleStatus

class LegeSuspensjonRulesTest {
    @Test
    fun `Er ikkje suspendert, Status OK`() {
        val result =
            LegeSuspensjonRules(
                    LegeSuspensjonPayload(sykmeldingId = "foo-bar", behandlerSuspendert = false)
                )
                .execute()

        assertEquals(result.first.treeResult.status, RuleStatus.OK)
        assertNull(result.first.treeResult.ruleOutcome)

        assertEquals(
            result.first.rulePath.map { it.rule to it.ruleResult },
            listOf(LegeSuspensjonRule.BEHANDLER_SUSPENDERT to false),
        )
        assertEquals(result.first.ruleInputs, mapOf("suspendert" to false))
    }

    @Test
    fun `Er suspendert, Status INVALID`() {
        val result =
            LegeSuspensjonRules(
                    LegeSuspensjonPayload(sykmeldingId = "foo-bar", behandlerSuspendert = true)
                )
                .execute()

        assertEquals(result.first.treeResult.status, RuleStatus.INVALID)
        assertEquals(
            result.first.treeResult.ruleOutcome,
            LegeSuspensjonRule.Outcomes.BEHANDLER_SUSPENDERT,
        )

        assertEquals(
            result.first.rulePath.map { it.rule to it.ruleResult },
            listOf(LegeSuspensjonRule.BEHANDLER_SUSPENDERT to true),
        )
        assertEquals(result.first.ruleInputs, mapOf("suspendert" to true))
    }
}
