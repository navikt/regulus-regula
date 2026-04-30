package no.nav.tsm.regulus.regula.juridisk

import java.time.ZonedDateTime

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
