package no.nav.tsm.regulus.regula.trees.pasientunder13

import java.time.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import no.nav.tsm.regulus.regula.executor.RuleStatus
import no.nav.tsm.regulus.regula.payload.FomTom
import no.nav.tsm.regulus.regula.trees.assertPath

class PasientUnder13RulesTest {
    @Test
    fun `Alt ok, Status OK`() {
        val person14Years = LocalDate.now().minusYears(14)
        val (result) =
            PasientUnder13Rules(
                    PasientUnder13RulePayload(
                        pasientFodselsdato = person14Years,
                        sykmeldingId = "foo-bar-baz",
                        perioder =
                            listOf(
                                FomTom(
                                    fom = LocalDate.now().minusDays(10),
                                    tom = LocalDate.now().plusDays(10),
                                )
                            ),
                    )
                )
                .execute()

        assertEquals(result.treeResult.status, RuleStatus.OK)
        assertPath(result.rulePath, listOf(PasientUnder13Rule.PASIENT_YNGRE_ENN_13 to false))
        assertEquals(result.ruleInputs, mapOf("pasientUnder13Aar" to false))

        assertNull(result.treeResult.ruleOutcome, null)
    }

    @Test
    fun `Pasient under 13 Aar, Status INVALID`() {
        val person12Years = LocalDate.now().minusYears(12)
        val (result) =
            PasientUnder13Rules(
                    PasientUnder13RulePayload(
                        pasientFodselsdato = person12Years,
                        sykmeldingId = "foo-bar-baz",
                        perioder =
                            listOf(
                                FomTom(
                                    fom = LocalDate.now().minusDays(10),
                                    tom = LocalDate.now().plusDays(10),
                                )
                            ),
                    )
                )
                .execute()

        assertEquals(result.treeResult.status, RuleStatus.INVALID)
        assertPath(result.rulePath, listOf(PasientUnder13Rule.PASIENT_YNGRE_ENN_13 to true))

        assertEquals(result.ruleInputs, mapOf("pasientUnder13Aar" to true))

        assertEquals(
            result.treeResult.ruleOutcome,
            PasientUnder13Rule.Outcomes.PASIENT_YNGRE_ENN_13,
        )
    }
}
