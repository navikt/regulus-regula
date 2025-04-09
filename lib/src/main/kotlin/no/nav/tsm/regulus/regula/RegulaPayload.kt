package no.nav.tsm.regulus.regula

import java.time.LocalDate
import java.time.LocalDateTime
import no.nav.tsm.regulus.regula.payload.Aktivitet
import no.nav.tsm.regulus.regula.payload.AnnenFravarsArsak
import no.nav.tsm.regulus.regula.payload.Diagnose
import no.nav.tsm.regulus.regula.payload.SykmelderGodkjenning
import no.nav.tsm.regulus.regula.payload.TidligereSykmelding

/** The entire payload is needed to apply the rules to this specific sykmelding. */
data class RegulaPayload(
    /** Generated by the application as early as possible. */
    val sykmeldingId: String,
    /** Provided by the health care professional in the sykmelding. */
    val hoveddiagnose: Diagnose?,
    /** Provided by the health care professional in the sykmelding. */
    val bidiagnoser: List<Diagnose>?,
    val annenFravarsArsak: AnnenFravarsArsak?,
    val aktivitet: List<Aktivitet>,
    val behandletTidspunkt: LocalDateTime,
    val utdypendeOpplysninger: Map<String, Map<String, Map<String, String>>>?,
    val tidligereSykmeldinger: List<TidligereSykmelding>,
    val kontaktPasientBegrunnelseIkkeKontakt: String?,
    val pasient: RegulaPasient,
    val meta: RegulaMeta,
    val sykmelder: RegulaSykmelder,
    val avsender: RegulaAvsender,
)

data class RegulaPasient(
    /** Kan være både FNR og DNR */
    val ident: String,
    /** Fødselsdatoen til pasienten SKAL komme rett fra PDL. */
    val fodselsdato: LocalDate,
)

/**
 * In the new architecture this is the same fnr as the RegulaSykmelder.fnr, in the legacy it can be
 * different
 */
sealed class RegulaAvsender {
    data class Finnes(val fnr: String) : RegulaAvsender()

    data object IngenAvsender : RegulaAvsender()
}

/** Values that are not directly in the sykmelding document, but assosiated with it * */
sealed class RegulaMeta {
    /** "Old" sykmelding process */
    data class LegacyMeta(
        val signaturdato: LocalDateTime,
        val mottattDato: LocalDateTime,
        val rulesetVersion: String?,
    ) : RegulaMeta()

    /** Evergreen meta, used primarily for syk-inn * */
    data class Meta(val sendtTidspunkt: LocalDateTime) : RegulaMeta()
}

sealed class RegulaSykmelder(open val fnr: String) {
    data class Finnes(
        /**
         * Er sykmelderen suspendert i btsys? Denne verdien SKAL komme rett fra btsys fra
         * konsumerende applikasjon.
         */
        val suspendert: Boolean,
        /**
         * Sykmelders autorisasjoner fra HPR-registeret. Disse verdiene skal være ferske og hentes
         * fra helsenettproxy fra konsumerende applikasjon.
         */
        val godkjenninger: List<SykmelderGodkjenning>,
        val legekontorOrgnr: String?,
        override val fnr: String,
    ) : RegulaSykmelder(fnr)

    data class FinnesIkke(override val fnr: String) : RegulaSykmelder(fnr)
}
