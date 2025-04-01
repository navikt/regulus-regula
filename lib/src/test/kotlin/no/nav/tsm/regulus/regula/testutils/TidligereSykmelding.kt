package no.nav.tsm.regulus.regula.testutils

import java.time.LocalDate
import no.nav.helse.diagnosekoder.Diagnosekoder
import no.nav.tsm.regulus.regula.RegulaStatus
import no.nav.tsm.regulus.regula.payload.Diagnose
import no.nav.tsm.regulus.regula.payload.RelevanteMerknader
import no.nav.tsm.regulus.regula.payload.TidligereSykmelding
import no.nav.tsm.regulus.regula.payload.TidligereSykmeldingAktivitet
import no.nav.tsm.regulus.regula.payload.TidligereSykmeldingMeta

internal fun testTidligereSykmelding(fom: LocalDate, tom: LocalDate): List<TidligereSykmelding> {
    return listOf(
        TidligereSykmelding(
            sykmeldingId = "foo-bar-baz",
            aktivitet = listOf(TidligereSykmeldingAktivitet.IkkeMulig(fom = fom, tom = tom)),
            hoveddiagnose = Diagnose(kode = "X01", system = Diagnosekoder.ICPC2_CODE),
            meta =
                TidligereSykmeldingMeta(
                    status = RegulaStatus.OK,
                    userAction = "SENDT",
                    merknader = null,
                ),
        )
    )
}

internal fun testTidligereSykmelding(
    dates: List<Pair<LocalDate, LocalDate>>,
    status: RegulaStatus = RegulaStatus.OK,
    userAction: String = "SENDT",
    merknader: List<RelevanteMerknader>? = null,
): List<TidligereSykmelding> {
    return dates.mapIndexed { index, fomTom ->
        TidligereSykmelding(
            sykmeldingId = "foo-bar-baz-$index",
            aktivitet =
                listOf(
                    TidligereSykmeldingAktivitet.IkkeMulig(fom = fomTom.first, tom = fomTom.second)
                ),
            hoveddiagnose = Diagnose(kode = "X01", system = Diagnosekoder.ICPC2_CODE),
            meta =
                TidligereSykmeldingMeta(
                    status = status,
                    userAction = userAction,
                    merknader = merknader,
                ),
        )
    }
}
