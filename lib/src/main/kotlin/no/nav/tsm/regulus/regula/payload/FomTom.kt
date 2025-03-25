package no.nav.tsm.regulus.regula.payload

import java.time.LocalDate

/** The simplest representation of a period, for rule trees that only care about ranges */
data class FomTom(val fom: LocalDate, val tom: LocalDate) : ClosedRange<LocalDate> {
    override val endInclusive: LocalDate = tom
    override val start: LocalDate = fom
}
