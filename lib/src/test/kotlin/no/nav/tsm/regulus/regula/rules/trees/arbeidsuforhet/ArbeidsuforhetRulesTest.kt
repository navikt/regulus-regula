package no.nav.tsm.regulus.regula.rules.trees.arbeidsuforhet

import java.time.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import no.nav.helse.diagnosekoder.Diagnosekoder
import no.nav.tsm.regulus.regula.executor.RuleStatus
import no.nav.tsm.regulus.regula.payload.Diagnose
import no.nav.tsm.regulus.regula.rules.trees.assertPath
import org.junit.jupiter.api.Nested

class ArbeidsuforhetRulesTest {

    val person31Years = LocalDate.now().minusYears(31)

    @Nested
    inner class DiagnoseTester {
        @Test
        fun `Hoveddiagnose is null and annen Fraværsårsak is null`() {
            val payload = testArbeidsuforhetPayload(hoveddiagnose = null, annenFraversArsak = null)

            val (result) = ArbeidsuforhetRules(payload).execute()

            assertEquals(result.treeResult.status, RuleStatus.INVALID)
            assertPath(
                result.rulePath,
                listOf(
                    ArbeidsuforhetRule.HOVEDDIAGNOSE_MANGLER to true,
                    ArbeidsuforhetRule.FRAVAERSGRUNN_MANGLER to true,
                ),
            )
            assertEquals(
                result.ruleInputs,
                mapOf("hoveddiagnoseMangler" to true, "fraversgrunnMangler" to true),
            )
            assertEquals(
                result.treeResult.ruleOutcome,
                ArbeidsuforhetRule.Outcomes.FRAVAERSGRUNN_MANGLER,
            )
        }

        @Test
        fun `Hoveddiagnose is null and annen Fraværsårsak is not null`() {
            val payload =
                testArbeidsuforhetPayload(
                    hoveddiagnose = null,
                    annenFraversArsak =
                        AnnenFraversArsak(
                            grunn = listOf("grunn1", "grunn2"),
                            beskrivelse = "beskrivelse",
                        ),
                )

            val (result) = ArbeidsuforhetRules(payload).execute()

            assertPath(
                result.rulePath,
                listOf(
                    ArbeidsuforhetRule.HOVEDDIAGNOSE_MANGLER to true,
                    ArbeidsuforhetRule.FRAVAERSGRUNN_MANGLER to false,
                    ArbeidsuforhetRule.UGYLDIG_KODEVERK_FOR_BIDIAGNOSE to false,
                ),
            )
            assertEquals(
                result.ruleInputs,
                mapOf(
                    "hoveddiagnoseMangler" to true,
                    "fraversgrunnMangler" to false,
                    "ugyldigKodeVerkBiDiagnose" to false,
                ),
            )
            assertEquals(result.treeResult.status, RuleStatus.OK)
            assertNull(result.treeResult.ruleOutcome)
        }

        @Test
        fun `Hoveddiagnose is null and annen Fraværsårsak beskrivelse is null and grunn is empty`() {
            val payload =
                testArbeidsuforhetPayload(
                    hoveddiagnose = null,
                    annenFraversArsak = AnnenFraversArsak(beskrivelse = null, grunn = emptyList()),
                )

            val (result) = ArbeidsuforhetRules(payload).execute()

            assertEquals(result.treeResult.status, RuleStatus.INVALID)
            assertPath(
                result.rulePath,
                listOf(
                    ArbeidsuforhetRule.HOVEDDIAGNOSE_MANGLER to true,
                    ArbeidsuforhetRule.FRAVAERSGRUNN_MANGLER to true,
                ),
            )
            assertEquals(
                result.ruleInputs,
                mapOf("hoveddiagnoseMangler" to true, "fraversgrunnMangler" to true),
            )
            assertEquals(
                result.treeResult.ruleOutcome,
                ArbeidsuforhetRule.Outcomes.FRAVAERSGRUNN_MANGLER,
            )
        }

        @Test
        fun `Mangler hoveddiagnose, annen fravarsgrunn, ugylidg diagnose`() {
            val payload =
                testArbeidsuforhetPayload(
                    hoveddiagnose = null,
                    annenFraversArsak =
                        AnnenFraversArsak(beskrivelse = "beskrivelse", grunn = emptyList()),
                    bidiagnoser =
                        listOf(Diagnose(system = "2.16.578.1.12.4.1.1.7170", kode = "R222222")),
                )

            val (result) = ArbeidsuforhetRules(payload).execute()

            assertEquals(result.treeResult.status, RuleStatus.INVALID)
            assertPath(
                result.rulePath,
                listOf(
                    ArbeidsuforhetRule.HOVEDDIAGNOSE_MANGLER to true,
                    ArbeidsuforhetRule.FRAVAERSGRUNN_MANGLER to false,
                    ArbeidsuforhetRule.UGYLDIG_KODEVERK_FOR_BIDIAGNOSE to true,
                ),
            )
            assertEquals(
                result.ruleInputs,
                mapOf(
                    "hoveddiagnoseMangler" to true,
                    "fraversgrunnMangler" to false,
                    "ugyldigKodeVerkBiDiagnose" to true,
                ),
            )
            assertEquals(
                result.treeResult.ruleOutcome,
                ArbeidsuforhetRule.Outcomes.UGYLDIG_KODEVERK_FOR_BIDIAGNOSE,
            )
        }

        @Test
        fun `All OK`() {
            val payload =
                testArbeidsuforhetPayload(
                    hoveddiagnose = Diagnose(system = Diagnosekoder.ICPC2_CODE, kode = "R24"),
                    bidiagnoser = listOf(Diagnose(system = Diagnosekoder.ICPC2_CODE, kode = "R24")),
                )

            val (result) = ArbeidsuforhetRules(payload).execute()

            assertEquals(result.treeResult.status, RuleStatus.OK)
            assertPath(
                result.rulePath,
                listOf(
                    ArbeidsuforhetRule.HOVEDDIAGNOSE_MANGLER to false,
                    ArbeidsuforhetRule.UGYLDIG_KODEVERK_FOR_HOVEDDIAGNOSE to false,
                    ArbeidsuforhetRule.ICPC_2_Z_DIAGNOSE to false,
                    ArbeidsuforhetRule.UGYLDIG_KODEVERK_FOR_BIDIAGNOSE to false,
                ),
            )
            assertEquals(
                result.ruleInputs,
                mapOf(
                    "hoveddiagnoseMangler" to false,
                    "ugyldigKodeverkHovedDiagnose" to false,
                    "icpc2ZDiagnose" to false,
                    "ugyldigKodeVerkBiDiagnose" to false,
                ),
            )
            assertNull(result.treeResult.ruleOutcome)
        }
    }

    @Test
    fun `Ugyldig kodeverk for hoveddiagnose, Status INVALID`() {
        val payload =
            testArbeidsuforhetPayload(
                hoveddiagnose = Diagnose(system = "2.16.578.1.12.4.1.1.9999", kode = "A09")
            )

        val (result) = ArbeidsuforhetRules(payload).execute()

        assertEquals(result.treeResult.status, RuleStatus.INVALID)
        assertPath(
            result.rulePath,
            listOf(
                ArbeidsuforhetRule.HOVEDDIAGNOSE_MANGLER to false,
                ArbeidsuforhetRule.UGYLDIG_KODEVERK_FOR_HOVEDDIAGNOSE to true,
            ),
        )
        assertEquals(
            result.ruleInputs,
            mapOf("hoveddiagnoseMangler" to false, "ugyldigKodeverkHovedDiagnose" to true),
        )
        assertEquals(
            result.treeResult.ruleOutcome,
            ArbeidsuforhetRule.Outcomes.UGYLDIG_KODEVERK_FOR_HOVEDDIAGNOSE,
        )
    }

    @Test
    fun `Diagnosen er icpc 2 z diagnose, Status INVALID`() {
        val payload =
            testArbeidsuforhetPayload(
                hoveddiagnose = Diagnose(system = Diagnosekoder.ICPC2_CODE, kode = "Z09")
            )

        val (result) = ArbeidsuforhetRules(payload).execute()

        assertEquals(result.treeResult.status, RuleStatus.INVALID)
        assertPath(
            result.rulePath,
            listOf(
                ArbeidsuforhetRule.HOVEDDIAGNOSE_MANGLER to false,
                ArbeidsuforhetRule.UGYLDIG_KODEVERK_FOR_HOVEDDIAGNOSE to false,
                ArbeidsuforhetRule.ICPC_2_Z_DIAGNOSE to true,
            ),
        )
        assertEquals(
            result.ruleInputs,
            mapOf(
                "hoveddiagnoseMangler" to false,
                "ugyldigKodeverkHovedDiagnose" to false,
                "icpc2ZDiagnose" to true,
            ),
        )
        assertEquals(result.treeResult.ruleOutcome, ArbeidsuforhetRule.Outcomes.ICPC_2_Z_DIAGNOSE)
    }

    @Test
    fun `HovedDiagnose og fraversgrunn mangler, Status INVALID`() {
        // TODO: This is exactly the same as one of the tests in DiagnoseTester, should be removed
        val payload = testArbeidsuforhetPayload(hoveddiagnose = null, annenFraversArsak = null)

        val (result) = ArbeidsuforhetRules(payload).execute()

        assertEquals(result.treeResult.status, RuleStatus.INVALID)
        assertPath(
            result.rulePath,
            listOf(
                ArbeidsuforhetRule.HOVEDDIAGNOSE_MANGLER to true,
                ArbeidsuforhetRule.FRAVAERSGRUNN_MANGLER to true,
            ),
        )
        assertEquals(
            result.ruleInputs,
            mapOf("hoveddiagnoseMangler" to true, "fraversgrunnMangler" to true),
        )
        assertEquals(
            result.treeResult.ruleOutcome,
            ArbeidsuforhetRule.Outcomes.FRAVAERSGRUNN_MANGLER,
        )
    }

    @Test
    fun `Ugyldig kodeVerk for biDiagnose, Status INVALID`() {
        val payload =
            testArbeidsuforhetPayload(
                hoveddiagnose = Diagnose(system = "2.16.578.1.12.4.1.1.7170", kode = "R24"),
                bidiagnoser = listOf(Diagnose(system = "2.16.578.1.12.4.1.1.7110", kode = "S09")),
            )

        val (result) = ArbeidsuforhetRules(payload).execute()

        assertEquals(result.treeResult.status, RuleStatus.INVALID)
        assertPath(
            result.rulePath,
            listOf(
                ArbeidsuforhetRule.HOVEDDIAGNOSE_MANGLER to false,
                ArbeidsuforhetRule.UGYLDIG_KODEVERK_FOR_HOVEDDIAGNOSE to false,
                ArbeidsuforhetRule.ICPC_2_Z_DIAGNOSE to false,
                ArbeidsuforhetRule.UGYLDIG_KODEVERK_FOR_BIDIAGNOSE to true,
            ),
        )
        assertEquals(
            result.ruleInputs,
            mapOf(
                "hoveddiagnoseMangler" to false,
                "ugyldigKodeverkHovedDiagnose" to false,
                "icpc2ZDiagnose" to false,
                "ugyldigKodeVerkBiDiagnose" to true,
            ),
        )
        assertEquals(
            result.treeResult.ruleOutcome,
            ArbeidsuforhetRule.Outcomes.UGYLDIG_KODEVERK_FOR_BIDIAGNOSE,
        )
    }

    @Test
    fun `Ugyldig Kodeverk for hovedDiagnose, Status INVALID`() {
        val payload =
            testArbeidsuforhetPayload(
                hoveddiagnose = Diagnose(system = "2.16.578.1.12.4.1.1.7110", kode = "Z09")
            )

        val (result) = ArbeidsuforhetRules(payload).execute()

        assertEquals(result.treeResult.status, RuleStatus.INVALID)
        assertPath(
            result.rulePath,
            listOf(
                ArbeidsuforhetRule.HOVEDDIAGNOSE_MANGLER to false,
                ArbeidsuforhetRule.UGYLDIG_KODEVERK_FOR_HOVEDDIAGNOSE to true,
            ),
        )
        assertEquals(
            result.ruleInputs,
            mapOf("hoveddiagnoseMangler" to false, "ugyldigKodeverkHovedDiagnose" to true),
        )
        assertEquals(
            result.treeResult.ruleOutcome,
            ArbeidsuforhetRule.Outcomes.UGYLDIG_KODEVERK_FOR_HOVEDDIAGNOSE,
        )
    }
}
