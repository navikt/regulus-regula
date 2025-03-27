package no.nav.tsm.regulus.regula.rules.trees.tilbakedatering.extras

import java.time.LocalDate
import no.nav.tsm.regulus.regula.payload.TidligereSykmelding
import no.nav.tsm.regulus.regula.rules.shared.getStartdatoFromRelevanteDatoer
import no.nav.tsm.regulus.regula.rules.shared.relevanteDatoer
import no.nav.tsm.regulus.regula.utils.allDaysBetween
import no.nav.tsm.regulus.regula.utils.isWorkingDaysBetween

internal data class Arbeidsgiverperiode(
    val isArbeidsgiverperiode: Boolean,
    val startdato: LocalDate,
    val dager: List<LocalDate>,
)

internal fun isArbeidsgiverperiode(
    earliestFom: LocalDate,
    latestTom: LocalDate,
    tidligereSykmeldinger: List<TidligereSykmelding>,
): Arbeidsgiverperiode {
    val datoer = relevanteDatoer(earliestFom, tidligereSykmeldinger)
    val startdato = getStartdatoFromRelevanteDatoer(earliestFom, datoer)

    val dager =
        getSykedagerForArbeidsgiverperiode(
            startDato = startdato,
            fom = earliestFom,
            tom = latestTom,
            allDates = datoer,
        )

    return Arbeidsgiverperiode(
        startdato = startdato,
        dager = dager,
        isArbeidsgiverperiode = dager.size <= 16,
    )
}

private fun getSykedagerForArbeidsgiverperiode(
    startDato: LocalDate,
    fom: LocalDate,
    tom: LocalDate,
    allDates: List<LocalDate>,
): List<LocalDate> {
    val datoer = allDates.sortedDescending().filter { it < fom && it >= startDato }
    val antallSykdagerForArbeidsgiverPeriode = allDaysBetween(fom, tom).toMutableList()

    if (antallSykdagerForArbeidsgiverPeriode.size > 16) {
        return antallSykdagerForArbeidsgiverPeriode.subList(0, 17)
    }

    val dager =
        antallSykdagerForArbeidsgiverPeriode.toMutableList().sortedDescending().toMutableList()
    var lastDate = fom

    for (currentDate in datoer) {
        if (!isWorkingDaysBetween(lastDate, currentDate)) {
            dager.addAll(allDaysBetween(currentDate, lastDate.minusDays(1)))
        } else {
            dager.add(currentDate)
        }
        lastDate = currentDate
        if (dager.size > 16) {
            break
        }
    }
    return dager
}
