package no.nav.tsm.regulus.regula.payload

import java.time.LocalDate

enum class SykmeldingPeriodeType {
    AKTIVITET_IKKE_MULIG,
    AVVENTENDE,
    BEHANDLINGSDAGER,
    GRADERT,
    REISETILSKUDD,
}

/**
 * A more complex representation of the sykmeldingsperioder, with a distinct union on the 5 types of
 * periods.
 */
sealed class SykmeldingPeriode(
    val type: SykmeldingPeriodeType,
    open val fom: LocalDate,
    open val tom: LocalDate,
) : ClosedRange<LocalDate> {
    data class AktivitetIkkeMulig(override val fom: LocalDate, override val tom: LocalDate) :
        SykmeldingPeriode(SykmeldingPeriodeType.AKTIVITET_IKKE_MULIG, fom = fom, tom = tom)

    data class Avventende(override val fom: LocalDate, override val tom: LocalDate) :
        SykmeldingPeriode(SykmeldingPeriodeType.AVVENTENDE, fom = fom, tom = tom)

    data class Behandlingsdager(override val fom: LocalDate, override val tom: LocalDate) :
        SykmeldingPeriode(SykmeldingPeriodeType.BEHANDLINGSDAGER, fom = fom, tom = tom)

    data class Gradert(override val fom: LocalDate, override val tom: LocalDate, val grad: Int) :
        SykmeldingPeriode(SykmeldingPeriodeType.GRADERT, fom = fom, tom = tom)

    data class Reisetilskudd(override val fom: LocalDate, override val tom: LocalDate) :
        SykmeldingPeriode(SykmeldingPeriodeType.REISETILSKUDD, fom = fom, tom = tom)

    override val start: LocalDate
        get() = fom

    override val endInclusive: LocalDate
        get() = tom
}
