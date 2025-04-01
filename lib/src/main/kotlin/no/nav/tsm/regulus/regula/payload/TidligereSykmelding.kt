package no.nav.tsm.regulus.regula.payload

import no.nav.tsm.regulus.regula.RegulaStatus

data class TidligereSykmelding(
    val sykmeldingId: String,
    val aktivitet: List<Aktivitet>,
    val hoveddiagnose: Diagnose?,
    val meta: TidligereSykmeldingMeta,
)

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
