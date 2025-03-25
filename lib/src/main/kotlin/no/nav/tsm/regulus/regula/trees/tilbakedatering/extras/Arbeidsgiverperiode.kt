package no.nav.tsm.regulus.regula.trees.tilbakedatering.extras

import java.time.LocalDate
import java.time.temporal.ChronoUnit
import no.nav.tsm.regulus.regula.payload.SykmeldingPeriodeType
import no.nav.tsm.regulus.regula.trees.tilbakedatering.TidligereSykmelding
import no.nav.tsm.regulus.regula.utils.allDaysBetween
import no.nav.tsm.regulus.regula.utils.earliestFom
import no.nav.tsm.regulus.regula.utils.isWorkingDaysBetween
import no.nav.tsm.regulus.regula.utils.latestTom

data class Arbeidsgiverperiode(
    val isArbeidsgiverperiode: Boolean,
    val startdato: LocalDate,
    val dager: List<LocalDate>,
)

fun isArbeidsgiverperiode(
    earliestFom: LocalDate,
    latestTom: LocalDate,
    tidligereSykmeldinger: List<TidligereSykmelding>,
): Arbeidsgiverperiode {
    val datoer = filterDates(earliestFom, tidligereSykmeldinger)

    var startdato = earliestFom
    datoer.forEach {
        if (ChronoUnit.DAYS.between(it, startdato) > 16) {
            return Arbeidsgiverperiode(
                startdato = startdato,
                isArbeidsgiverperiode = false,
                dager =
                    getSykedagerForArbeidsgiverperiode(
                        startDato = startdato,
                        fom = earliestFom,
                        tom = latestTom,
                        allDates = datoer,
                    ),
            )
        } else {
            startdato = it
        }
    }

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

private fun filterDates(
    startdato: LocalDate,
    tidligereSykmeldinger: List<TidligereSykmelding>,
): List<LocalDate> {
    return tidligereSykmeldinger
        .filter { it.perioder.latestTom() > startdato.minusWeeks(40).minusDays(0) }
        .filter { it.perioder.earliestFom() < startdato }
        // TODO: Put this responsibility on the consumer of the lib?
        // .filter { it.behandlingsutfall.status != RegelStatusDTO.INVALID }
        // TODO: Put this responsibility on the consumer of the lib?
        // .filterNot {
        //     !it.merknader.isNullOrEmpty() &&
        //         it.merknader.any { merknad ->
        //             merknad.type == MerknadType.UGYLDIG_TILBAKEDATERING.toString() ||
        //                 merknad.type ==
        //                     MerknadType.TILBAKEDATERING_KREVER_FLERE_OPPLYSNINGER.toString()
        //         }
        // }
        // TODO: Put this responsibility on the consumer of the lib?
        // .filter { it.sykmeldingStatus.statusEvent != "AVBRUTT" }
        .map { it ->
            it.perioder
                .filter { it.type != SykmeldingPeriodeType.AVVENTENDE }
                .flatMap { allDaysBetween(it.fom, it.tom) }
        }
        .flatten()
        .distinct()
        .sortedDescending()
}

fun getSykedagerForArbeidsgiverperiode(
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
