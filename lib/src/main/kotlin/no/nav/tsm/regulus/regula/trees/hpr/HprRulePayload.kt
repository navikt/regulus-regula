package no.nav.tsm.regulus.regula.trees.hpr

import no.nav.tsm.regulus.regula.trees.validation.FomTom
import java.time.LocalDate
import java.time.LocalDateTime

data class HprRulePayload(
    val sykmeldingId: String,
    val behandler: Behandler,
    val perioder: List<FomTom>,
    val startdato: LocalDate?,
    val signaturDato: LocalDateTime,
)

data class Behandler(
    val godkjenninger: List<Godkjenning>
)

data class Godkjenning(
    val autorisasjon: Autorisasjon?,
    val helsepersonellkategori: Kode?,
    val tillegskompetanse: List<Tillegskompetanse>?
)


data class Tillegskompetanse(
    val avsluttetStatus: Kode?,
    val eTag: String?,
    val gyldig: Periode?,
    val id: Int?,
    val type: Kode?
)

data class Periode(val fra: LocalDateTime?, val til: LocalDateTime?)

data class Kode(
    val aktiv: Boolean,
    val oid: Int,
    val verdi: String?,
)

data class Autorisasjon(
    val aktiv: Boolean?,
    val oid: Int?,
    val verdi: String?
)
