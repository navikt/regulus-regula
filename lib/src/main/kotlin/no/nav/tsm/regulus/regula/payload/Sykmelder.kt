package no.nav.tsm.regulus.regula.payload

import java.time.LocalDateTime

data class SykmelderGodkjenning(
    val autorisasjon: SykmelderKode?,
    val helsepersonellkategori: SykmelderKode?,
    val tillegskompetanse: List<SykmelderTilleggskompetanse>?,
)

data class SykmelderTilleggskompetanse(
    val avsluttetStatus: SykmelderKode?,
    val gyldig: SykmelderPeriode?,
    val type: SykmelderKode?,
)

data class SykmelderPeriode(val fra: LocalDateTime?, val til: LocalDateTime?)

data class SykmelderKode(val aktiv: Boolean, val oid: Int, val verdi: String?)
