package no.nav.tsm.regulus.regula.trees.hpr

import java.time.LocalDate
import java.time.LocalDateTime
import no.nav.tsm.regulus.regula.payload.BasePayload
import no.nav.tsm.regulus.regula.payload.FomTom

internal data class HprRulePayload(
    override val sykmeldingId: String,
    val behandler: Behandler,
    val perioder: List<FomTom>,
    val startdato: LocalDate?,
    val signaturdato: LocalDateTime,
) : BasePayload

internal data class Behandler(val godkjenninger: List<Godkjenning>)

internal data class Godkjenning(
    val autorisasjon: Kode?,
    val helsepersonellkategori: Kode?,
    val tillegskompetanse: List<Tilleggskompetanse>?,
)

internal data class Tilleggskompetanse(
    val avsluttetStatus: Kode?,
    val eTag: String?,
    val gyldig: Periode?,
    val id: Int?,
    val type: Kode?,
)

internal data class Periode(val fra: LocalDateTime?, val til: LocalDateTime?)

internal data class Kode(val aktiv: Boolean, val oid: Int, val verdi: String?)
