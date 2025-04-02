package no.nav.tsm.regulus.regula

import java.time.LocalDate

data class RegulaJuridiskHenvisning(
    val lovverk: Lovverk,
    val paragraf: String,
    val ledd: Int?,
    val punktum: Int?,
    val bokstav: String?,
)

enum class Lovverk(val navn: String, val kortnavn: String, val lovverksversjon: LocalDate) {
    FOLKETRYGDLOVEN(
        navn = "Lov om folketrygd",
        kortnavn = "Folketrygdloven",
        lovverksversjon = LocalDate.of(2022, 1, 1),
    ),
    FORVALTNINGSLOVEN(
        navn = "Lov om behandlingsmåten i forvaltningssaker",
        kortnavn = "Forvaltningsloven",
        lovverksversjon = LocalDate.of(2022, 1, 1),
    ),
    HELSEPERSONELLOVEN(
        navn = "Lov om helsepersonell m.v.",
        kortnavn = "Helsepersonelloven",
        lovverksversjon = LocalDate.of(2022, 1, 1),
    ),
}
