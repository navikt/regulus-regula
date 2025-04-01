package no.nav.tsm.regulus.regula.rules.trees.tilbakedatering.extras

import java.time.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import no.nav.helse.diagnosekoder.Diagnosekoder
import no.nav.tsm.regulus.regula.RegulaStatus
import no.nav.tsm.regulus.regula.payload.Diagnose
import no.nav.tsm.regulus.regula.payload.TidligereSykmelding
import no.nav.tsm.regulus.regula.payload.TidligereSykmeldingAktivitet
import no.nav.tsm.regulus.regula.payload.TidligereSykmeldingMeta
import no.nav.tsm.regulus.regula.testutils.april
import no.nav.tsm.regulus.regula.testutils.january

class ArbeidsgiverperiodeKtTest {
    @Test
    fun `sykmelding 16 dager skal ta med alle`() {
        val result =
            isArbeidsgiverperiode(
                earliestFom = 1.january(2020),
                latestTom = 16.january(2020),
                tidligereSykmeldinger = emptyList(),
            )

        assertTrue(result.isArbeidsgiverperiode)
        assertTrue(result.dager.size == 16)
        assertEquals(result.startdato, 1.january(2020))
    }

    @Test
    fun `sykmelding på 17 dager skal bare ta med 17`() {
        val result =
            isArbeidsgiverperiode(
                earliestFom = 1.january(2020),
                latestTom = 17.january(2020),
                tidligereSykmeldinger = emptyList(),
            )

        assertFalse(result.isArbeidsgiverperiode)
        assertTrue(result.dager.size == 17)
        assertEquals(result.startdato, 1.january(2020))
    }

    @Test
    fun `sykmelding 18 dager skal bare ta med 17 første`() {
        val result =
            isArbeidsgiverperiode(
                earliestFom = 1.january(2020),
                latestTom = 18.january(2020),
                tidligereSykmeldinger = emptyList(),
            )

        assertFalse(result.isArbeidsgiverperiode)
        assertTrue(result.dager.size == 17)
        assertEquals(result.startdato, 1.january(2020))
    }

    @Test
    fun `sykmelding 1 dager hver 16 dag`() {
        val fom = 1.january(2020)
        val tidligereSykmelding: List<TidligereSykmelding> =
            (1L until 20L)
                .map { fom.minusDays(16 * it) }
                .map { testTidligereSykmelding(fom = it, tom = it) }

        val result =
            isArbeidsgiverperiode(
                earliestFom = 1.january(2020),
                latestTom = 18.january(2020),
                tidligereSykmeldinger = tidligereSykmelding,
            )

        assertFalse(result.isArbeidsgiverperiode)
        assertTrue(result.dager.size == 17)
        assertEquals(result.startdato, 4.april(2019))
    }

    @Test
    fun `sykmelding mandag - fredag for to uker, skal være 12 dager`() {
        val fom = 13.january(2020)
        val tom = 17.january(2020)
        val tidligereSykmelding: List<TidligereSykmelding> =
            listOf(testTidligereSykmelding(fom = 6.january(2020), tom = 10.january(2020)))

        val result =
            isArbeidsgiverperiode(
                earliestFom = fom,
                latestTom = tom,
                tidligereSykmeldinger = tidligereSykmelding,
            )

        assertTrue(result.isArbeidsgiverperiode)
        assertTrue(result.dager.size == 12)
        assertEquals(result.startdato, 6.january(2020))
    }

    @Test
    fun `sykmelding tirsdag - fredag for to uker, skal være 9 dager`() {
        val fom = 14.january(2020)
        val tom = 17.january(2020)
        val tidligereSykmelding: List<TidligereSykmelding> =
            listOf(testTidligereSykmelding(fom = 6.january(2020), tom = 10.january(2020)))

        val result =
            isArbeidsgiverperiode(
                earliestFom = fom,
                latestTom = tom,
                tidligereSykmeldinger = tidligereSykmelding,
            )

        assertTrue(result.isArbeidsgiverperiode)
        assertTrue(result.dager.size == 9)
        assertEquals(result.startdato, 6.january(2020))
    }

    @Test
    fun `sykmelding mandag - fredag for tre uker, skal være 19 (17) dager`() {
        val fom = 20.january(2020)
        val tom = 24.january(2020)
        val tidligereSykmelding =
            listOf(
                testTidligereSykmelding(fom = 6.january(2020), tom = 10.january(2020)),
                testTidligereSykmelding(fom = 13.january(2020), tom = 17.january(2020)),
            )

        val result =
            isArbeidsgiverperiode(
                earliestFom = fom,
                latestTom = tom,
                tidligereSykmeldinger = tidligereSykmelding,
            )

        assertFalse(result.isArbeidsgiverperiode)
        assertTrue(result.dager.size == 17)
        assertEquals(result.startdato, 6.january(2020))
    }
}

private fun testTidligereSykmelding(fom: LocalDate, tom: LocalDate, id: String = "foo-bar") =
    TidligereSykmelding(
        sykmeldingId = id,
        hoveddiagnose = Diagnose(kode = "L89", system = Diagnosekoder.ICPC2_CODE),
        aktivitet = listOf(TidligereSykmeldingAktivitet.IkkeMulig(fom, tom)),
        meta =
            TidligereSykmeldingMeta(
                status = RegulaStatus.OK,
                userAction = "SENDT",
                merknader = null,
            ),
    )
