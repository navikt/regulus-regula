package no.nav.tsm.regulus.regula.trees.hpr

import java.time.LocalDateTime
import kotlin.test.Test
import kotlin.test.assertEquals
import no.nav.tsm.regulus.regula.executor.RuleStatus
import no.nav.tsm.regulus.regula.trees.assertPath

class HprRulesTest {
    @Test
    fun `har ikke aktiv autorisasjon, Status INVALID`() {
        val behandler = testBehandler(BehandlerScenarios.INAKTIV_LEGE)
        val (result) =
            HprRules(
                    HprRulePayload(
                        sykmeldingId = "foo-bar",
                        behandler = behandler,
                        perioder = emptyList(),
                        startdato = null,
                        signaturdato = LocalDateTime.now(),
                    )
                )
                .execute()

        assertEquals(result.treeResult.status, RuleStatus.INVALID)
        assertPath(result.rulePath, listOf(HprRule.BEHANDLER_GYLIDG_I_HPR to false))

        assertEquals(result.ruleInputs, mapOf("behandlerGodkjenninger" to behandler.godkjenninger))
    }
}
