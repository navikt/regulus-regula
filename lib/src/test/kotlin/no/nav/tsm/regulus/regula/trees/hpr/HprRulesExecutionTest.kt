package no.nav.tsm.regulus.regula.trees.hpr

import java.time.LocalDateTime
import kotlin.test.Test
import kotlin.test.assertEquals
import no.nav.tsm.regulus.regula.executor.RuleStatus

class HprRulesExecutionTest {
    @Test
    fun `har ikke aktiv autorisasjon, Status INVALID`() {
        val behandler = testBehandler(BehandlerScenarios.INAKTIV_LEGE)
        val result =
            HprRulesExecution(
                    HprRulePayload(
                        sykmeldingId = "foo-bar",
                        behandler = behandler,
                        perioder = emptyList(),
                        startdato = null,
                        signaturDato = LocalDateTime.now(),
                    )
                )
                .runRules()

        assertEquals(result.first.treeResult.status, RuleStatus.INVALID)
        assertEquals(
            result.first.rulePath.map { it.rule to it.ruleResult },
            listOf(HprRule.BEHANDLER_GYLIDG_I_HPR to false),
        )

        assertEquals(
            result.first.ruleInputs,
            mapOf("behandlerGodkjenninger" to behandler.godkjenninger),
        )
    }
}
