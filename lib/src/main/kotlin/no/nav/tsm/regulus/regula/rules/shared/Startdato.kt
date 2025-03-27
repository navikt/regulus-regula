package no.nav.tsm.regulus.regula.rules.shared

import java.time.LocalDate
import java.time.temporal.ChronoUnit
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
        .filter { it.perioder.latestTom() > earliestFom.minusWeeks(40).minusDays(0) }
        .filter { it.perioder.earliestFom() < earliestFom }
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
