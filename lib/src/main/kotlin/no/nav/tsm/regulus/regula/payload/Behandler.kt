package no.nav.tsm.regulus.regula.payload

import java.time.LocalDateTime

data class BehandlerGodkjenning(
    val autorisasjon: BehandlerKode?,
    val helsepersonellkategori: BehandlerKode?,
    val tillegskompetanse: List<BehandlerTilleggskompetanse>?,
)

data class BehandlerTilleggskompetanse(
    val avsluttetStatus: BehandlerKode?,
    val gyldig: BehandlerPeriode?,
    val type: BehandlerKode?,
)

data class BehandlerPeriode(val fra: LocalDateTime?, val til: LocalDateTime?)

data class BehandlerKode(val aktiv: Boolean, val oid: Int, val verdi: String?)
