package no.nav.tsm.regulus.regula.trees.hpr

import java.time.LocalDate
import java.time.LocalDateTime
import no.nav.tsm.regulus.regula.executor.BasePayload
import no.nav.tsm.regulus.regula.trees.validation.FomTom

data class HprRulePayload(
    override val sykmeldingId: String,
    val behandler: Behandler,
    val perioder: List<FomTom>,
    val startdato: LocalDate?,
    val signaturdato: LocalDateTime,
) : BasePayload

data class Behandler(val godkjenninger: List<Godkjenning>)

data class Godkjenning(
    val autorisasjon: Kode?,
    val helsepersonellkategori: Kode?,
    val tillegskompetanse: List<Tilleggskompetanse>?,
)

data class Tilleggskompetanse(
    val avsluttetStatus: Kode?,
    val eTag: String?,
    val gyldig: Periode?,
    val id: Int?,
    val type: Kode?,
)

data class Periode(val fra: LocalDateTime?, val til: LocalDateTime?)

data class Kode(val aktiv: Boolean, val oid: Int, val verdi: String?)
