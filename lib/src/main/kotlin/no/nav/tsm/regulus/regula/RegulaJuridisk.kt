package no.nav.tsm.regulus.regula

import java.time.LocalDate
import java.time.ZonedDateTime

enum class JuridiskUtfall {
    VILKAR_OPPFYLT,
    VILKAR_IKKE_OPPFYLT,
    VILKAR_UAVKLART,
}

data class RegulaJuridiskVurdering(
    val henvisning: JuridiskHenvisning,
    val utfall: JuridiskUtfall,
    val fodselsnummer: String,
    val tidsstempel: ZonedDateTime,
    val input: Map<String, Any>,
)

data class JuridiskHenvisning(
    val lovverk: JuridiskHenvisningLovverk,
    val paragraf: String,
    val ledd: Int?,
    val punktum: Int?,
    val bokstav: String?,
)

enum class JuridiskHenvisningLovverk(
    val navn: String,
    val kortnavn: String,
    val lovverksversjon: LocalDate,
) {
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
