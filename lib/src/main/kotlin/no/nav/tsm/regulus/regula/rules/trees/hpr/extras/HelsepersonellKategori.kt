package no.nav.tsm.regulus.regula.rules.trees.hpr.extras

internal enum class HelsepersonellKategori(
    val beskrivendeHelsepersonellKategoriKategoriVerdi: String,
    val verdi: String,
) {
    KIROPRAKTOR("Kiropraktor", "KI"),
    LEGE("Lege", "LE"),
    MANUELLTERAPEUT("ManuellTerapeut", "MT"),
    FYSIOTERAPAEUT("Fysioterapeut", "FT"),
    TANNLEGE("Tannlege", "TL"),
}
