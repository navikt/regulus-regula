package no.nav.tsm.regulus.regula.rules.shared

import java.time.LocalDate
import java.time.temporal.ChronoUnit
import no.nav.tsm.regulus.regula.RegulaStatus
import no.nav.tsm.regulus.regula.payload.RelevanteMerknader
import no.nav.tsm.regulus.regula.payload.SykmeldingPeriodeType
import no.nav.tsm.regulus.regula.payload.TidligereSykmelding
import no.nav.tsm.regulus.regula.utils.allDaysBetween
import no.nav.tsm.regulus.regula.utils.earliestFom
import no.nav.tsm.regulus.regula.utils.latestTom

internal fun getStartdatoFromTidligereSykmeldinger(
    earliestFom: LocalDate,
    tidligereSykmeldinger: List<TidligereSykmelding>,
): LocalDate =
    getStartdatoFromRelevanteDatoer(
        earliestFom,
        relevanteDatoer = relevanteDatoer(earliestFom, tidligereSykmeldinger),
    )

internal fun getStartdatoFromRelevanteDatoer(
    earliestFom: LocalDate,
    relevanteDatoer: List<LocalDate>,
): LocalDate {
    var startdato = earliestFom
    relevanteDatoer.forEach {
        if (ChronoUnit.DAYS.between(it, startdato) > 16) {
            return startdato
        } else {
            startdato = it
        }
    }
    return startdato
}

internal fun relevanteDatoer(
    earliestFom: LocalDate,
    tidligereSykmeldinger: List<TidligereSykmelding>,
): List<LocalDate> {
    return tidligereSykmeldinger
        .filter { it.aktivitet.latestTom() > earliestFom.minusWeeks(40).minusDays(0) }
        .filter { it.aktivitet.earliestFom() < earliestFom }
        .filter { it.meta.status != RegulaStatus.INVALID }
        .filter { it.meta.userAction != "AVBRUTT" }
        .filter {
            it.meta.merknader.isNullOrEmpty() ||
                listOf(
                        RelevanteMerknader.TILBAKEDATERING_KREVER_FLERE_OPPLYSNINGER,
                        RelevanteMerknader.UGYLDIG_TILBAKEDATERING,
                    )
                    .intersect(it.meta.merknader)
                    .isEmpty()
        }
        .map { it ->
            it.aktivitet
                .filter { it.type != SykmeldingPeriodeType.AVVENTENDE }
                .flatMap { allDaysBetween(it.fom, it.tom) }
        }
        .flatten()
        .distinct()
        .sortedDescending()
}
