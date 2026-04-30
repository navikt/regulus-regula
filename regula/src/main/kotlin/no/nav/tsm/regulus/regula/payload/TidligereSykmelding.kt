package no.nav.tsm.regulus.regula.payload

import java.time.LocalDate
import no.nav.tsm.regulus.regula.RegulaStatus

data class TidligereSykmelding(
    val sykmeldingId: String,
    val aktivitet: List<TidligereSykmeldingAktivitet>,
    val hoveddiagnose: Diagnose?,
    val meta: TidligereSykmeldingMeta,
)

/**
 * En enklere variasjon av @see [Aktivitet], tidligere sykmeldinger trenger ikke de spesifikk
 * inforamsjon om behandlingsdager og avvnentende informasjon.
 */
sealed class TidligereSykmeldingAktivitet(
    val type: SykmeldingPeriodeType,
    open val fom: LocalDate,
    open val tom: LocalDate,
) : ClosedRange<LocalDate> {
    data class IkkeMulig(override val fom: LocalDate, override val tom: LocalDate) :
        TidligereSykmeldingAktivitet(
            SykmeldingPeriodeType.AKTIVITET_IKKE_MULIG,
            fom = fom,
            tom = tom,
        )

    data class Avventende(override val fom: LocalDate, override val tom: LocalDate) :
        TidligereSykmeldingAktivitet(SykmeldingPeriodeType.AVVENTENDE, fom = fom, tom = tom)

    data class Behandlingsdager(override val fom: LocalDate, override val tom: LocalDate) :
        TidligereSykmeldingAktivitet(SykmeldingPeriodeType.BEHANDLINGSDAGER, fom = fom, tom = tom)

    data class Gradert(override val fom: LocalDate, override val tom: LocalDate, val grad: Int) :
        TidligereSykmeldingAktivitet(SykmeldingPeriodeType.GRADERT, fom = fom, tom = tom)

    data class Reisetilskudd(override val fom: LocalDate, override val tom: LocalDate) :
        TidligereSykmeldingAktivitet(SykmeldingPeriodeType.REISETILSKUDD, fom = fom, tom = tom)

    /**
     * Brukes for å representere ugyldige unions i gammel arkitektur. For eksempel sykmeldinger som
     * ikke kan bli noen av underkategoriene. Dette lar reglene slå ut på ugyldig periode,
     * istedenfor at konsumeren av dette biblioteket skal krasje.
     */
    data class Ugyldig(override val fom: LocalDate, override val tom: LocalDate) :
        TidligereSykmeldingAktivitet(SykmeldingPeriodeType.INVALID, fom = fom, tom = tom)

    override val start: LocalDate
        get() = fom

    override val endInclusive: LocalDate
        get() = tom
}

data class TidligereSykmeldingMeta(
    /** Regelstatus for sykmeldingen, se @see [RegulaStatus] */
    val status: RegulaStatus,
    /**
     * Hva brukeren valgte å gjøre med sykmeldingen, dette er typisk AVBRUTT, SENDT eller BEKREFTET
     */
    val userAction: String,
    /**
     * Merknader av typen @see [RelevanteMerknader], dersom det ikke er relevante merknader kan hele
     * listen være null eller tom.
     */
    val merknader: List<RelevanteMerknader>?,
)

enum class RelevanteMerknader {
    UGYLDIG_TILBAKEDATERING,
    TILBAKEDATERING_KREVER_FLERE_OPPLYSNINGER,
    UNDER_BEHANDLING,
}
