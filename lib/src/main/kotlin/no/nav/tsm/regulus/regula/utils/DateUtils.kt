package no.nav.tsm.regulus.regula.utils

import no.nav.tsm.regulus.regula.trees.validation.FomTom
import java.time.LocalDate
import java.time.temporal.ChronoUnit

fun daysBetween(fom: LocalDate, tom: LocalDate): Long = ChronoUnit.DAYS.between(fom, tom)

fun List<FomTom>.earliestFom(): LocalDate = map { it.fom }.sorted().first()

fun List<FomTom>.latestTom(): LocalDate = map { it.tom }.sorted().last()
