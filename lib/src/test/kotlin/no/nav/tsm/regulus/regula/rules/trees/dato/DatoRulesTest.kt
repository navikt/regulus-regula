package no.nav.tsm.regulus.regula.rules.trees.dato

import java.time.LocalDate
import java.time.LocalDateTime
import kotlin.test.*
import no.nav.tsm.regulus.regula.dsl.RuleStatus
import no.nav.tsm.regulus.regula.dsl.getOutcome
import no.nav.tsm.regulus.regula.executor.ExecutionMode
import no.nav.tsm.regulus.regula.payload.Aktivitet
import no.nav.tsm.regulus.regula.rules.trees.assertPath

class DatoRulesTest {

    @Test
    fun `Alt ok, Status OK`() {
        val now = LocalDateTime.now()
        val perioder =
            listOf(Aktivitet.IkkeMulig(fom = LocalDate.now(), tom = LocalDate.now().plusDays(7)))

        val (result) =
            DatoRules(
                    DatoRulePayload(
                        sykmeldingId = "sykmeldingId",
                        aktivitet = perioder,
                        signaturdato = now,
                    )
                )
                .execute(ExecutionMode.NORMAL)

        assertEquals(result.treeResult.status, RuleStatus.OK)
        assertPath(
            result.rulePath,
            listOf(
                DatoRule.FREMDATERT to false,
                DatoRule.TILBAKEDATERT_MER_ENN_3_AR to false,
                DatoRule.TOTAL_VARIGHET_OVER_ETT_AAR to false,
            ),
        )

        assertEquals(
            mapOf(
                "genereringsTidspunkt" to now,
                "fom" to perioder.first().fom,
                "tom" to perioder.first().tom,
                "fremdatert" to false,
                "tilbakeDatertMerEnn3AAr" to false,
                "varighetOver1AAr" to false,
            ),
            result.ruleInputs,
        )

        assertNull(result.treeResult.getOutcome())
    }

    @Test
    fun `Fremdater over 30 dager, Status INVALID`() {
        val now = LocalDateTime.now()
        val perioder =
            listOf(
                Aktivitet.IkkeMulig(
                    fom = LocalDate.now().plusDays(31),
                    tom = LocalDate.now().plusDays(37),
                )
            )

        val (result) =
            DatoRules(
                    DatoRulePayload(
                        sykmeldingId = "sykmeldingId",
                        aktivitet = perioder,
                        signaturdato = now,
                    )
                )
                .execute(ExecutionMode.NORMAL)

        assertEquals(result.treeResult.status, RuleStatus.INVALID)
        assertPath(result.rulePath, listOf(DatoRule.FREMDATERT to true))

        assertEquals(
            mapOf(
                "genereringsTidspunkt" to now,
                "fom" to perioder.first().fom,
                "fremdatert" to true,
            ),
            result.ruleInputs,
        )

        assertEquals(result.treeResult.getOutcome(), DatoRule.Outcomes.FREMDATERT)
    }

    @Test
    fun `Varighet over 1 år, Status INVALID`() {
        val now = LocalDateTime.now()
        val perioder =
            listOf(
                Aktivitet.IkkeMulig(fom = LocalDate.now(), tom = LocalDate.now()),
                Aktivitet.IkkeMulig(
                    fom = LocalDate.now().plusDays(1),
                    tom = LocalDate.now().plusDays(366),
                ),
            )

        val (result) =
            DatoRules(
                    DatoRulePayload(
                        sykmeldingId = "sykmeldingId",
                        aktivitet = perioder,
                        signaturdato = now,
                    )
                )
                .execute(ExecutionMode.NORMAL)

        assertEquals(result.treeResult.status, RuleStatus.INVALID)
        assertPath(
            result.rulePath,
            listOf(
                DatoRule.FREMDATERT to false,
                DatoRule.TILBAKEDATERT_MER_ENN_3_AR to false,
                DatoRule.TOTAL_VARIGHET_OVER_ETT_AAR to true,
            ),
        )

        assertEquals(
            mapOf(
                "genereringsTidspunkt" to now,
                "fom" to perioder.first().fom,
                "tom" to perioder.last().tom,
                "fremdatert" to false,
                "tilbakeDatertMerEnn3AAr" to false,
                "varighetOver1AAr" to true,
            ),
            result.ruleInputs,
        )

        assertEquals(result.treeResult.getOutcome(), DatoRule.Outcomes.TOTAL_VARIGHET_OVER_ETT_AAR)
    }
}
