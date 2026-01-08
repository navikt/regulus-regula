package no.nav.tsm.regulus.regula.rules.trees.arbeidsuforhet

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import no.nav.tsm.diagnoser.ICD10
import no.nav.tsm.diagnoser.ICPC2
import no.nav.tsm.diagnoser.ICPC2B
import no.nav.tsm.regulus.regula.dsl.RuleStatus
import no.nav.tsm.regulus.regula.dsl.getOutcome
import no.nav.tsm.regulus.regula.executor.ExecutionMode
import no.nav.tsm.regulus.regula.payload.AnnenFravarsArsak
import no.nav.tsm.regulus.regula.payload.Diagnose
import no.nav.tsm.regulus.regula.rules.trees.assertPath
import org.junit.jupiter.api.Nested

class ArbeidsuforhetRulesTest {

    @Nested
    inner class DiagnoseTester {
        @Test
        fun `Hoveddiagnose is null and annen Fraværsårsak is null`() {
            val payload = testArbeidsuforhetPayload(hoveddiagnose = null, annenFravarsArsak = null)

            val result = ArbeidsuforhetRules(payload).execute(ExecutionMode.NORMAL)

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
                result.treeResult.getOutcome(),
                ArbeidsuforhetRule.Outcomes.FRAVAERSGRUNN_MANGLER,
            )
        }

        @Test
        fun `Papirmode - Hoveddiagnose is null and annen Fraværsårsak is null`() {
            val payload = testArbeidsuforhetPayload(hoveddiagnose = null, annenFravarsArsak = null)

            val result = ArbeidsuforhetRules(payload).execute(ExecutionMode.PAPIR)

            assertEquals(result.treeResult.status, RuleStatus.MANUAL_PROCESSING)
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
                result.treeResult.getOutcome(),
                ArbeidsuforhetRule.Outcomes.FRAVAERSGRUNN_MANGLER,
            )
        }

        @Test
        fun `Hoveddiagnose is null and annen Fraværsårsak is not null`() {
            val payload =
                testArbeidsuforhetPayload(
                    hoveddiagnose = null,
                    annenFravarsArsak =
                        AnnenFravarsArsak(
                            grunn = listOf("grunn1", "grunn2"),
                            beskrivelse = "beskrivelse",
                        ),
                )

            val result = ArbeidsuforhetRules(payload).execute(ExecutionMode.NORMAL)

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
            assertNull(result.treeResult.getOutcome())
        }

        @Test
        fun `Hoveddiagnose is null and annen Fraværsårsak beskrivelse is null and grunn is empty`() {
            val payload =
                testArbeidsuforhetPayload(
                    hoveddiagnose = null,
                    annenFravarsArsak = AnnenFravarsArsak(beskrivelse = null, grunn = emptyList()),
                )

            val result = ArbeidsuforhetRules(payload).execute(ExecutionMode.NORMAL)

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
                result.treeResult.getOutcome(),
                ArbeidsuforhetRule.Outcomes.FRAVAERSGRUNN_MANGLER,
            )
        }

        @Test
        fun `Mangler hoveddiagnose, annen fravarsgrunn, ugylidg diagnose`() {
            val payload =
                testArbeidsuforhetPayload(
                    hoveddiagnose = null,
                    annenFravarsArsak =
                        AnnenFravarsArsak(beskrivelse = "beskrivelse", grunn = emptyList()),
                    bidiagnoser =
                        listOf(Diagnose(system = "2.16.578.1.12.4.1.1.7170", kode = "R222222")),
                )

            val result = ArbeidsuforhetRules(payload).execute(ExecutionMode.NORMAL)

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
                result.treeResult.getOutcome(),
                ArbeidsuforhetRule.Outcomes.UGYLDIG_KODEVERK_FOR_BIDIAGNOSE,
            )
        }

        @Test
        fun `All OK`() {
            val payload =
                testArbeidsuforhetPayload(
                    hoveddiagnose = Diagnose(system = ICPC2.OID, kode = "R24"),
                    bidiagnoser = listOf(Diagnose(system = ICPC2.OID, kode = "R24")),
                )

            val result = ArbeidsuforhetRules(payload).execute(ExecutionMode.NORMAL)

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
                    "diagnoseSystem" to ICPC2.OID,
                    "diagnoseKode" to "R24",
                    "icpc2ZDiagnose" to false,
                    "ugyldigKodeVerkBiDiagnose" to false,
                ),
            )
            assertNull(result.treeResult.getOutcome())
        }
    }

    @Test
    fun `Ugyldig kodeverk for hoveddiagnose, Status INVALID`() {
        val payload =
            testArbeidsuforhetPayload(
                hoveddiagnose = Diagnose(system = "2.16.578.1.12.4.1.1.9999", kode = "A09")
            )

        val result = ArbeidsuforhetRules(payload).execute(ExecutionMode.NORMAL)

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
            mapOf("hoveddiagnoseMangler" to false, "diagnoseSystem" to "2.16.578.1.12.4.1.1.9999"),
        )
        assertEquals(
            result.treeResult.getOutcome(),
            ArbeidsuforhetRule.Outcomes.UGYLDIG_KODEVERK_FOR_HOVEDDIAGNOSE,
        )
    }

    @Test
    fun `Diagnosen er icpc 2 z diagnose, Status INVALID`() {
        val payload =
            testArbeidsuforhetPayload(hoveddiagnose = Diagnose(system = ICPC2.OID, kode = "Z09"))

        val result = ArbeidsuforhetRules(payload).execute(ExecutionMode.NORMAL)

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
                "diagnoseSystem" to ICPC2.OID,
                "diagnoseKode" to "Z09",
                "icpc2ZDiagnose" to true,
            ),
        )
        assertEquals(result.treeResult.getOutcome(), ArbeidsuforhetRule.Outcomes.ICPC_2_Z_DIAGNOSE)
    }

    @Test
    fun `HovedDiagnose og fraversgrunn mangler, Status INVALID`() {
        // TODO: This is exactly the same as one of the tests in DiagnoseTester, should be removed
        val payload = testArbeidsuforhetPayload(hoveddiagnose = null, annenFravarsArsak = null)

        val result = ArbeidsuforhetRules(payload).execute(ExecutionMode.NORMAL)

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
            result.treeResult.getOutcome(),
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

        val result = ArbeidsuforhetRules(payload).execute(ExecutionMode.NORMAL)

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
                "diagnoseSystem" to ICPC2.OID,
                "diagnoseKode" to "R24",
                "icpc2ZDiagnose" to false,
                "ugyldigKodeVerkBiDiagnose" to true,
            ),
        )
        assertEquals(
            result.treeResult.getOutcome(),
            ArbeidsuforhetRule.Outcomes.UGYLDIG_KODEVERK_FOR_BIDIAGNOSE,
        )
    }

    @Test
    fun `Ugyldig Kodeverk for hovedDiagnose, Status INVALID`() {
        val payload =
            testArbeidsuforhetPayload(
                hoveddiagnose = Diagnose(system = "2.16.578.1.12.4.1.1.7110", kode = "Z09")
            )

        val result = ArbeidsuforhetRules(payload).execute(ExecutionMode.NORMAL)

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
            mapOf(
                "hoveddiagnoseMangler" to false,
                "diagnoseSystem" to ICD10.OID,
                "diagnoseKode" to "Z09",
            ),
        )
        assertEquals(
            result.treeResult.getOutcome(),
            ArbeidsuforhetRule.Outcomes.UGYLDIG_KODEVERK_FOR_HOVEDDIAGNOSE,
        )
    }

    @Test
    fun `ICPC2B skal godkjennes som ICPC2`() {
        val payload =
            testArbeidsuforhetPayload(
                hoveddiagnose = Diagnose(system = ICPC2B.OID, kode = "A03.0005")
            )

        val result = ArbeidsuforhetRules(payload).execute(ExecutionMode.NORMAL)

        println(result)

        assertEquals(RuleStatus.OK, result.treeResult.status)
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
                "diagnoseSystem" to ICPC2B.OID,
                "diagnoseKode" to "A03.0005",
                "icpc2ZDiagnose" to false,
                "ugyldigKodeVerkBiDiagnose" to false,
            ),
        )
    }
}
