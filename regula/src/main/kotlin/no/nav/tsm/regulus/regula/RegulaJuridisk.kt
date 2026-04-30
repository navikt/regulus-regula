package no.nav.tsm.regulus.regula

import java.time.ZonedDateTime
import no.nav.tsm.regulus.regula.juridisk.JuridiskHenvisning
import no.nav.tsm.regulus.regula.juridisk.JuridiskUtfall
import no.nav.tsm.regulus.regula.juridisk.JuridiskVurdering

data class RegulaJuridiskVurdering(
    val henvisning: JuridiskHenvisning,
    val utfall: JuridiskUtfall,
    val fodselsnummer: String,
    val tidsstempel: ZonedDateTime,
    val input: Map<String, Any>,
)

fun RegulaJuridiskVurdering.toJuridiskVurdering(
    id: String,
    eventName: String,
    version: String,
    kilde: String,
    versjonAvKode: String,
    sporing: Map<String, String>,
): JuridiskVurdering {
    return JuridiskVurdering(
        id = id,
        eventName = eventName,
        version = version,
        kilde = kilde,
        versjonAvKode = versjonAvKode,
        sporing = sporing,
        fodselsnummer = this.fodselsnummer,
        juridiskHenvisning = this.henvisning,
        input = this.input,
        tidsstempel = this.tidsstempel,
        utfall = this.utfall,
    )
}
