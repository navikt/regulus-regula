package no.nav.tsm.regulus.regula.testutils

import java.time.LocalDate
import no.nav.helse.diagnosekoder.Diagnosekoder
import no.nav.tsm.regulus.regula.payload.Diagnose
import no.nav.tsm.regulus.regula.payload.SykmeldingPeriode
import no.nav.tsm.regulus.regula.payload.TidligereSykmelding

internal fun testTidligereSykmelding(fom: LocalDate, tom: LocalDate): List<TidligereSykmelding> {
    return listOf(
        TidligereSykmelding(
            sykmeldingId = "foo-bar-baz",
            perioder = listOf(SykmeldingPeriode.AktivitetIkkeMulig(fom = fom, tom = tom)),
            hoveddiagnose = Diagnose(kode = "X01", system = Diagnosekoder.ICPC2_CODE),
        )
    )
}

internal fun testTidligereSykmelding(
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
