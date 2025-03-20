package no.nav.tsm.regulus.regula.utils

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import no.nav.tsm.regulus.regula.payload.FomTom

fun daysBetween(fom: LocalDate, tom: LocalDate): Long = ChronoUnit.DAYS.between(fom, tom)

fun workdaysBetween(a: LocalDate, b: LocalDate): Int =
    (1..<ChronoUnit.DAYS.between(a, b))
        .map { a.plusDays(it) }
        .count { it.dayOfWeek !in arrayOf(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY) }

fun List<FomTom>.earliestFom(): LocalDate = map { it.fom }.sorted().first()

fun List<FomTom>.latestTom(): LocalDate = map { it.tom }.sorted().last()
