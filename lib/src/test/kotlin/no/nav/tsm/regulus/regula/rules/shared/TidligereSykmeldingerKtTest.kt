package no.nav.tsm.regulus.regula.rules.shared

import kotlin.test.Test
import kotlin.test.assertEquals
import no.nav.helse.diagnosekoder.Diagnosekoder
import no.nav.tsm.regulus.regula.RegulaStatus
import no.nav.tsm.regulus.regula.payload.Diagnose
import no.nav.tsm.regulus.regula.payload.RelevanteMerknader
import no.nav.tsm.regulus.regula.testutils.january
import no.nav.tsm.regulus.regula.testutils.testTidligereSykmelding

class TidligereSykmeldingerKtTest {

    val testDiagnose = { Diagnose(kode = "X01", system = Diagnosekoder.ICPC2_CODE) }

    @Test
    fun `shall not do anything with a OK sendt sykmelding`() {
        val result =
            testTidligereSykmelding(
                    dates = listOf(16.january(2023) to 31.january(2023)),
                    hoveddiagnose = testDiagnose(),
                    status = RegulaStatus.OK,
                    userAction = "SENDT",
                    merknader = emptyList(),
                )
                .onlyRelevantWithSameDiagnosis(testDiagnose())

        assertEquals(result.size, 1)
    }

    @Test
    fun `shall filter invalid sykmelding`() {
        val result =
            testTidligereSykmelding(
                    dates = listOf(16.january(2023) to 31.january(2023)),
                    hoveddiagnose = testDiagnose(),
                    status = RegulaStatus.INVALID,
                    userAction = "SENDT",
                    merknader = emptyList(),
                )
                .onlyRelevantWithSameDiagnosis(testDiagnose())

        assertEquals(result.size, 0)
    }

    @Test
    fun `shall filter diagnoseless sykmelding`() {
        val result =
            testTidligereSykmelding(
                    dates = listOf(16.january(2023) to 31.january(2023)),
                    hoveddiagnose = null,
                    status = RegulaStatus.OK,
                    userAction = "SENDT",
                    merknader = emptyList(),
                )
                .onlyRelevantWithSameDiagnosis(testDiagnose())

        assertEquals(result.size, 0)
    }

    @Test
    fun `shall filter differing diagnose`() {
        val result =
            testTidligereSykmelding(
                    dates = listOf(16.january(2023) to 31.january(2023)),
                    hoveddiagnose = Diagnose(system = Diagnosekoder.ICPC2_CODE, kode = "X01"),
                    status = RegulaStatus.OK,
                    userAction = "SENDT",
                    merknader = emptyList(),
                )
                .onlyRelevantWithSameDiagnosis(
                    Diagnose(system = Diagnosekoder.ICPC2_CODE, kode = "X02")
                )

        assertEquals(result.size, 0)
    }

    @Test
    fun `shall filter with any merknad sykmelding`() {
        val result =
            testTidligereSykmelding(
                    dates = listOf(16.january(2023) to 31.january(2023)),
                    hoveddiagnose = testDiagnose(),
                    status = RegulaStatus.OK,
                    userAction = "SENDT",
                    merknader = listOf(RelevanteMerknader.UGYLDIG_TILBAKEDATERING),
                )
                .onlyRelevantWithSameDiagnosis(testDiagnose())

        assertEquals(result.size, 0)
    }
}
