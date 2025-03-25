package no.nav.tsm.regulus.regula.utils

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.ChronoUnit

fun daysBetween(fom: LocalDate, tom: LocalDate): Long = ChronoUnit.DAYS.between(fom, tom)

fun workdaysBetween(a: LocalDate, b: LocalDate): Int =
    (1..<ChronoUnit.DAYS.between(a, b))
        .map { a.plusDays(it) }
        .count { it.dayOfWeek !in arrayOf(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY) }

fun allDaysBetween(fom: LocalDate, tom: LocalDate): List<LocalDate> =
    (0..ChronoUnit.DAYS.between(fom, tom)).map { fom.plusDays(it) }

fun List<ClosedRange<LocalDate>>.earliestFom(): LocalDate = map { it.start }.sorted().first()

fun List<ClosedRange<LocalDate>>.latestTom(): LocalDate = map { it.endInclusive }.sorted().last()
