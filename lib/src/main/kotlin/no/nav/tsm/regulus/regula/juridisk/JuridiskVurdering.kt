package no.nav.tsm.regulus.regula.juridisk

import java.time.ZonedDateTime
import no.nav.tsm.regulus.regula.JuridiskHenvisning
import no.nav.tsm.regulus.regula.JuridiskUtfall
import no.nav.tsm.regulus.regula.RegulaJuridiskVurdering

/**
 * The final "Juridisk Vurdering" that is produced to the "paragraf i kode" topic on kafka. This
 * object shall be used directly by teamsykmelding-pik (consuming), as well as any app that produces
 * directly to the internal kafka topic.
 *
 * This will have to be created by the application to provide the last metadata.
 */
data class JuridiskVurdering(
    val id: String,
    val eventName: String,
    val version: String,
    val kilde: String,
    val versjonAvKode: String,
    val fodselsnummer: String,
    val juridiskHenvisning: JuridiskHenvisning,
    val sporing: Map<String, String>,
    val input: Map<String, Any>,
    val tidsstempel: ZonedDateTime,
    val utfall: JuridiskUtfall,
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
