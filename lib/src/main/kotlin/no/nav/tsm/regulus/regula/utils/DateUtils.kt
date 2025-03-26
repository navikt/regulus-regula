package no.nav.tsm.regulus.regula.utils

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.ChronoUnit

internal fun daysBetween(fom: LocalDate, tom: LocalDate): Long = ChronoUnit.DAYS.between(fom, tom)

internal fun workdaysBetween(a: LocalDate, b: LocalDate): Int =
    (1..<ChronoUnit.DAYS.between(a, b))
        .map { a.plusDays(it) }
        .count { it.dayOfWeek !in arrayOf(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY) }

internal fun isWorkingDaysBetween(firstFom: LocalDate, periodeTom: LocalDate): Boolean {
    val daysBetween = ChronoUnit.DAYS.between(periodeTom, firstFom).toInt()
    if (daysBetween < 0) return true
    return when (firstFom.dayOfWeek) {
        DayOfWeek.MONDAY -> daysBetween > 3
        DayOfWeek.SUNDAY -> daysBetween > 2
        else -> daysBetween > 1
    }
}

internal fun allDaysBetween(fom: LocalDate, tom: LocalDate): List<LocalDate> =
    (0..ChronoUnit.DAYS.between(fom, tom)).map { fom.plusDays(it) }

internal fun List<ClosedRange<LocalDate>>.earliestFom(): LocalDate =
    map { it.start }.sorted().first()

internal fun List<ClosedRange<LocalDate>>.latestTom(): LocalDate =
    map { it.endInclusive }.sorted().last()
