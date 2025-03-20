package no.nav.tsm.regulus.regula.payload

import java.time.LocalDate

data class FomTom(val fom: LocalDate, val tom: LocalDate) : ClosedRange<LocalDate> {
    override val endInclusive: LocalDate = tom
    override val start: LocalDate = fom
}
