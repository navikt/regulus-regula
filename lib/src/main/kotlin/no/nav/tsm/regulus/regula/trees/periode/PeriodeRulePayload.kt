package no.nav.tsm.regulus.regula.trees.periode

import java.time.LocalDate
import java.time.LocalDateTime
import no.nav.tsm.regulus.regula.payload.BasePayload

internal data class PeriodeRulePayload(
    override val sykmeldingId: String,
    val perioder: List<Periode>,
    val behandletTidspunkt: LocalDateTime,
    val receivedDate: LocalDateTime,
) : BasePayload

// TODO: Use reusable Periode
internal data class Periode(
    val fom: LocalDate,
    val tom: LocalDate,
    val aktivitetIkkeMulig: AktivitetIkkeMulig?,
    val avventendeInnspillTilArbeidsgiver: String?,
    val behandlingsdager: Int?,
    val gradert: Gradert?,
    val reisetilskudd: Boolean,
) : ClosedRange<LocalDate> {
    override val endInclusive: LocalDate = tom
    override val start: LocalDate = fom
}

internal data class AktivitetIkkeMulig(
    val medisinskArsak: MedisinskArsak?,
    val arbeidsrelatertArsak: ArbeidsrelatertArsak?,
)

internal data class ArbeidsrelatertArsak(
    val beskrivelse: String?,
    val arsak: List<ArbeidsrelatertArsakType>,
)

enum class ArbeidsrelatertArsakType(
    val codeValue: String,
    val text: String,
    val oid: String = "2.16.578.1.12.4.1.1.8132",
) {
    MANGLENDE_TILRETTELEGGING("1", "Manglende tilrettelegging på arbeidsplassen"),
    ANNET("9", "Annet"),
}

internal data class MedisinskArsak(val beskrivelse: String?, val arsak: List<MedisinskArsakType>)

enum class MedisinskArsakType(
    val codeValue: String,
    val text: String,
    val oid: String = "2.16.578.1.12.4.1.1.8133",
) {
    TILSTAND_HINDRER_AKTIVITET("1", "Helsetilstanden hindrer pasienten i å være i aktivitet"),
    AKTIVITET_FORVERRER_TILSTAND("2", "Aktivitet vil forverre helsetilstanden"),
    AKTIVITET_FORHINDRER_BEDRING("3", "Aktivitet vil hindre/forsinke bedring av helsetilstanden"),
    ANNET("9", "Annet"),
}

internal data class Gradert(val reisetilskudd: Boolean, val grad: Int)
