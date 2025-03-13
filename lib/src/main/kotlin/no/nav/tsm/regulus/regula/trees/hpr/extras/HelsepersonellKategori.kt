package no.nav.tsm.regulus.regula.trees.hpr.extras

enum class HelsepersonellKategori(
    val beskrivendeHelsepersonellKategoriKategoriVerdi: String,
    val verdi: String
) {
    KIROPRAKTOR("Kiropraktor", "KI"),
    LEGE("Lege", "LE"),
    MANUELLTERAPEUT("ManuellTerapeut", "MT"),
    FYSIOTERAPAEUT("Fysioterapeut", "FT"),
    TANNLEGE("Tannlege", "TL"),
}
