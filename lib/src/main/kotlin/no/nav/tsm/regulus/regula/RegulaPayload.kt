package no.nav.tsm.regulus.regula

import java.time.LocalDate
import java.time.LocalDateTime
import no.nav.tsm.regulus.regula.payload.AnnenFravarsArsak
import no.nav.tsm.regulus.regula.payload.BehandlerGodkjenning
import no.nav.tsm.regulus.regula.payload.Diagnose
import no.nav.tsm.regulus.regula.payload.SykmeldingPeriode
import no.nav.tsm.regulus.regula.payload.TidligereSykmelding

/** The entire payload needed to apply the rules to this specific sykmelding. */
data class RegulaPayload(
    val sykmeldingId: String,
    val hoveddiagnose: Diagnose?,
    val bidiagnoser: List<Diagnose>?,
    val annenFravarsArsak: AnnenFravarsArsak?,
    val perioder: List<SykmeldingPeriode>,
    val utdypendeOpplysninger: Map<String, Map<String, Map<String, String>>>?,
    val tidligereSykmeldinger: List<TidligereSykmelding>,
    val kontaktPasientBegrunnelseIkkeKontakt: String?,
    val pasient: RegulaPasient,
    val meta: RegulaMeta,
    val behandler: RegulaBehandler,
    val avsender: RegulaAvsender,
)

data class RegulaPasient(
    /** Kan være både FNR og DNR */
    val ident: String,
    /** Fødselsdatoen til pasienten SKAL komme rett fra PDL. */
    val fodselsdato: LocalDate,
)

data class RegulaAvsender(val fnr: String)

/** Values that are not directly in the sykmelding document, but assosiated with it */
data class RegulaMeta(
    val signaturdato: LocalDateTime,
    val mottattDato: LocalDateTime,
    val behandletTidspunkt: LocalDateTime,
    val rulesetVersion: String?,
)

data class RegulaBehandler(
    /**
     * Er behandleren suspendert i btsys? Denne verdien SKAL komme rett fra btsys fra konsumerende
     * applikasjon.
     */
    val suspendert: Boolean,
    /**
     * Behandlerens autorisasjoner fra HPR-registeret. Disse verdiene skal være ferske og hentes fra
     * helsenettproxy fra konsumerende applikasjon.
     */
    val godkjenninger: List<BehandlerGodkjenning>,
    val legekontorOrgnr: String?,
    val fnr: String,
)
