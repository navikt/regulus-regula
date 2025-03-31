package no.nav.tsm.regulus.regula.rules.trees.tilbakedatering.extras

import java.time.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import no.nav.helse.diagnosekoder.Diagnosekoder
import no.nav.tsm.regulus.regula.payload.Aktivitet
import no.nav.tsm.regulus.regula.payload.Diagnose
import no.nav.tsm.regulus.regula.payload.TidligereSykmelding
import no.nav.tsm.regulus.regula.testutils.*

private fun testTidligereSykmelding(fom: LocalDate, tom: LocalDate) =
    TidligereSykmelding(
        sykmeldingId = "foo-bar",
        hoveddiagnose = Diagnose(kode = "L89", system = Diagnosekoder.ICPC2_CODE),
        aktivitet = listOf(Aktivitet.IkkeMulig(fom, tom)),
    )

class ForlengelseKtTest {

    @Test
    fun `en dag i mellom er ikke forlengelse`() {
        val result =
            isForlengelse(
                perioder = listOf(Aktivitet.IkkeMulig(17.february(2023), 28.february(2023))),
                hoveddiagnose = Diagnose(kode = "L89", system = Diagnosekoder.ICPC2_CODE),
                tidligereSykmeldinger =
                    listOf(
                        testTidligereSykmelding(1.january(2023), 31.january(2023)),
                        testTidligereSykmelding(1.february(2023), 15.february(2023)),
                    ),
            )

        assertEquals(result, listOf())
    }

    @Test
    fun `kant i kant er forlengelse`() {
        val result =
            isForlengelse(
                perioder = listOf(Aktivitet.IkkeMulig(16.february(2023), 28.february(2023))),
                hoveddiagnose = Diagnose(kode = "L89", system = Diagnosekoder.ICPC2_CODE),
                tidligereSykmeldinger =
                    listOf(
                        testTidligereSykmelding(1.january(2023), 31.january(2023)),
                        testTidligereSykmelding(1.february(2023), 15.february(2023)),
                    ),
            )

        assertEquals(
            result,
            listOf(Forlengelse("foo-bar", 1.february(2023), 15.february(2023), null)),
        )
    }

    @Test
    fun `kant i kant med flere sykmeldinger med samme sluttdato og er forlengelse`() {
        val result =
            isForlengelse(
                perioder = listOf(Aktivitet.IkkeMulig(16.february(2023), 28.february(2023))),
                hoveddiagnose = Diagnose(kode = "L89", system = Diagnosekoder.ICPC2_CODE),
                tidligereSykmeldinger =
                    listOf(
                        testTidligereSykmelding(1.january(2023), 31.january(2023)),
                        testTidligereSykmelding(1.february(2023), 15.february(2023)),
                        testTidligereSykmelding(1.february(2023), 15.february(2023)),
                    ),
            )

        assertEquals(
            result,
            listOf(
                Forlengelse("foo-bar", 1.february(2023), 15.february(2023), null),
                Forlengelse("foo-bar", 1.february(2023), 15.february(2023), null),
            ),
        )
    }

    @Test
    fun `sykmelding forlengelse hensyntar helg`() {
        val result =
            isForlengelse(
                hoveddiagnose = Diagnose(kode = "L89", system = Diagnosekoder.ICPC2_CODE),
                tidligereSykmeldinger =
                    listOf(
                        testTidligereSykmelding(1.january(2023), 31.january(2023)),
                        testTidligereSykmelding(
                            1.february(2023),
                            // Fredag
                            17.february(2023),
                        ),
                    ),
                perioder =
                    listOf(
                        Aktivitet.IkkeMulig(
                            // Mandag, skal være "kant" med 17. februar, som er fredag
                            20.february(2023),
                            28.february(2023),
                        )
                    ),
            )

        assertEquals(
            result,
            listOf(Forlengelse("foo-bar", 1.february(2023), 17.february(2023), null)),
        )
    }

    @Test
    fun `sykmelding forlengelse hensyntar overlapp`() {
        val result =
            isForlengelse(
                perioder = listOf(Aktivitet.IkkeMulig(17.february(2023), 28.february(2023))),
                hoveddiagnose = Diagnose(kode = "L89", system = Diagnosekoder.ICPC2_CODE),
                tidligereSykmeldinger =
                    listOf(
                        testTidligereSykmelding(1.january(2023), 31.january(2023)),
                        testTidligereSykmelding(1.february(2023), 17.february(2023)),
                    ),
            )

        assertEquals(
            result,
            listOf(Forlengelse("foo-bar", 1.february(2023), 17.february(2023), null)),
        )
    }

    @Test
    fun `sykmelding forlengelse hensyntar overlapp ekstra dag`() {
        val result =
            isForlengelse(
                perioder = listOf(Aktivitet.IkkeMulig(10.february(2023), 28.february(2023))),
                tidligereSykmeldinger =
                    listOf(
                        testTidligereSykmelding(1.january(2023), 31.january(2023)),
                        testTidligereSykmelding(1.february(2023), 17.february(2023)),
                    ),
                hoveddiagnose = Diagnose(kode = "L89", system = Diagnosekoder.ICPC2_CODE),
            )

        assertEquals(
            result,
            listOf(Forlengelse("foo-bar", 1.february(2023), 17.february(2023), null)),
        )
    }

    @Test
    fun `sykmelding forlengelse hensyntar direkte overlapp`() {
        val result =
            isForlengelse(
                perioder = listOf(Aktivitet.IkkeMulig(1.february(2023), 17.february(2023))),
                hoveddiagnose = Diagnose(kode = "L89", system = Diagnosekoder.ICPC2_CODE),
                tidligereSykmeldinger =
                    listOf(testTidligereSykmelding(1.february(2023), 17.february(2023))),
            )

        assertEquals(
            result,
            listOf(Forlengelse("foo-bar", 1.february(2023), 17.february(2023), null)),
        )
    }
}
