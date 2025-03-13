package no.nav.tsm.regulus.regula.utils

import java.time.LocalDate
import java.time.temporal.ChronoUnit

fun daysBetween(fom: LocalDate, tom: LocalDate): Long = ChronoUnit.DAYS.between(fom, tom)
