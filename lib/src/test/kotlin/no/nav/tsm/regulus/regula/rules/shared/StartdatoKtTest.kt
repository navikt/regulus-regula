package no.nav.tsm.regulus.regula.rules.shared

import java.time.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import no.nav.helse.diagnosekoder.Diagnosekoder
import no.nav.tsm.regulus.regula.payload.Diagnose
import no.nav.tsm.regulus.regula.payload.SykmeldingPeriode
import no.nav.tsm.regulus.regula.rules.trees.tilbakedatering.TidligereSykmelding
import no.nav.tsm.regulus.regula.testutils.*

class StartdatoKtTest {
    @Test
    fun `happy case - uten tidligere sykmeldinger`() {
        val startdato =
            getStartdatoFromTidligereSykmeldinger(
                earliestFom = 1.january(2023),
                tidligereSykmeldinger = listOf(),
            )

        assertEquals(startdato, 1.january(2023))
    }

    @Test
    fun `med en sykmelding kant til kant`() {
        val startdato =
            getStartdatoFromTidligereSykmeldinger(
                earliestFom = 16.january(2023),
                tidligereSykmeldinger = testTidligereSykmelding(1.january(2023), 15.january(2023)),
            )

        assertEquals(startdato, 1.january(2023))
    }

    @Test
    fun `med en sykmelding med 16 dager mellomrom`() {
        val startdato =
            getStartdatoFromTidligereSykmeldinger(
                earliestFom = 22.january(2023),
                tidligereSykmeldinger = testTidligereSykmelding(1.january(2023), 5.january(2023)),
            )

        assertEquals(startdato, 22.january(2023))
    }

    @Test
    fun `med en sykmelding med 15 dager mellomrom`() {
        val startdato =
            getStartdatoFromTidligereSykmeldinger(
                earliestFom = 21.january(2023),
                tidligereSykmeldinger = testTidligereSykmelding(1.january(2023), 5.january(2023)),
            )

        assertEquals(startdato, 1.january(2023))
    }

    @Test
    fun `test med sykmelding langt frem i tid`() {
        val startdato =
            getStartdatoFromTidligereSykmeldinger(
                earliestFom = 4.january(2023),
                tidligereSykmeldinger = testTidligereSykmelding(5.january(2023), 5.september(2023)),
            )

        assertEquals(startdato, 4.january(2023))
    }

    @Test
    fun `tilbakedatert sykmelding`() {
        val startdato =
            getStartdatoFromTidligereSykmeldinger(
                earliestFom = 1.january(2023),
                tidligereSykmeldinger = testTidligereSykmelding(1.february(2023), 28.february(2023)),
            )

        assertEquals(startdato, 1.january(2023))
    }

    @Test
    fun `Flere sykmeldinger fra syfosmregister`() {
        val startdato =
            getStartdatoFromTidligereSykmeldinger(
                earliestFom = 1.may(2023),
                tidligereSykmeldinger =
                    testTidligereSykmelding(
                        listOf(
                            1.january(2023) to 31.january(2023),
                            1.february(2023) to 28.february(2023),
                            1.march(2023) to 31.march(2023),
                            1.april(2023) to 30.april(2023),
                        )
                    ),
            )

        assertEquals(startdato, 1.january(2023))
    }

    @Test
    fun `Flere sykmeldinger fra syfosmregister med gap`() {
        val startdato =
            getStartdatoFromTidligereSykmeldinger(
                earliestFom = 1.may(2023),
                tidligereSykmeldinger =
                    testTidligereSykmelding(
                        listOf(
                            1.january(2023) to 31.january(2023),
                            1.february(2023) to 28.february(2023),
                            17.march(2023) to 31.march(2023),
                            1.april(2023) to 30.april(2023),
                        )
                    ),
            )

        assertEquals(startdato, 17.march(2023))
    }

    @Test
    fun `Flere sykmeldinger fra syfosmregister med avventende sykmelding`() {
        val startdato =
            getStartdatoFromTidligereSykmeldinger(
                earliestFom = 1.may(2023),
                tidligereSykmeldinger =
                    testTidligereSykmelding(
                        listOf(
                            1.january(2023) to 31.january(2023),
                            1.february(2023) to 28.february(2023),
                            // mars filtreres bort
                            1.april(2023) to 30.april(2023),
                        )
                    ),
            )

        assertEquals(startdato, 1.april(2023))
    }

    @Test
    fun `Flere sykmeldinger fra syfosmregister med flere perioder uten gap`() {
        val startdato =
            getStartdatoFromTidligereSykmeldinger(
                earliestFom = 1.may(2023),
                tidligereSykmeldinger =
                    testTidligereSykmelding(
                        listOf(
                            1.january(2023) to 31.january(2023),
                            1.february(2023) to 5.april(2023), // dekker alle ekstraPerioder
                            1.april(2023) to 30.april(2023),
                        )
                    ),
            )

        assertEquals(startdato, 1.january(2023))
    }

    @Test
    fun `Flere sykmeldinger fra syfosmregister med flere perioder`() {
        val startdato =
            getStartdatoFromTidligereSykmeldinger(
                earliestFom = 1.may(2023),
                tidligereSykmeldinger =
                    testTidligereSykmelding(
                        listOf(
                            16.january(2023) to 20.february(2023),
                            6.april(2023) to 30.april(2023),
                        )
                    ),
            )

        assertEquals(startdato, 6.april(2023))
    }

    @Test
    fun `Sykmelding fra syfosmregister med status AVBRUTT`() {
        val startdato =
            getStartdatoFromTidligereSykmeldinger(
                earliestFom = 1.march(2023),
                tidligereSykmeldinger =
                    testTidligereSykmelding(
                        listOf(
                            16.january(2023) to 31.january(2023)
                            // februar filtreres bort
                        )
                    ),
            )

        assertEquals(startdato, 1.march(2023))
    }

    @Test
    fun `Sykmelding fra syfosmregister tilbakedatert`() {
        val startdato =
            getStartdatoFromTidligereSykmeldinger(
                earliestFom = 1.february(2023),
                tidligereSykmeldinger =
                    testTidligereSykmelding(
                        listOf(
                            16.january(2023) to 31.january(2023)
                            // 1.jan til 15.jan filtreres bort
                        )
                    ),
            )

        assertEquals(startdato, 16.january(2023))
    }
}

private fun testTidligereSykmelding(fom: LocalDate, tom: LocalDate): List<TidligereSykmelding> {
    return listOf(
        TidligereSykmelding(
            sykmeldingId = "foo-bar-baz",
            perioder = listOf(SykmeldingPeriode.AktivitetIkkeMulig(fom = fom, tom = tom)),
            hoveddiagnose = Diagnose(kode = "X01", system = Diagnosekoder.ICPC2_CODE),
        )
    )
}

private fun testTidligereSykmelding(
    dates: List<Pair<LocalDate, LocalDate>>
): List<TidligereSykmelding> {
    return dates.mapIndexed { index, fomTom ->
        TidligereSykmelding(
            sykmeldingId = "foo-bar-baz-$index",
            perioder =
                listOf(
                    SykmeldingPeriode.AktivitetIkkeMulig(fom = fomTom.first, tom = fomTom.second)
                ),
            hoveddiagnose = Diagnose(kode = "X01", system = Diagnosekoder.ICPC2_CODE),
        )
    }
}
