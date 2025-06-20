package no.nav.tsm.regulus.regula

import java.time.LocalDate
import java.time.LocalDateTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import no.nav.helse.diagnosekoder.Diagnosekoder
import no.nav.tsm.regulus.regula.executor.ExecutionMode
import no.nav.tsm.regulus.regula.payload.Aktivitet
import no.nav.tsm.regulus.regula.payload.AnnenFravarsArsak
import no.nav.tsm.regulus.regula.payload.BehandlerGodkjenning
import no.nav.tsm.regulus.regula.payload.BehandlerKode
import no.nav.tsm.regulus.regula.payload.Diagnose
import no.nav.tsm.regulus.regula.payload.TidligereSykmelding
import no.nav.tsm.regulus.regula.payload.TidligereSykmeldingAktivitet
import no.nav.tsm.regulus.regula.payload.TidligereSykmeldingMeta

class RegulaKtTest {

    @Test
    fun `simple complete happy path`() {
        val result =
            executeRegulaRules(
                RegulaPayload(
                    sykmeldingId = "123456789",
                    hoveddiagnose = Diagnose(kode = "A01", system = Diagnosekoder.ICPC2_CODE),
                    bidiagnoser = listOf(Diagnose(kode = "B02", system = Diagnosekoder.ICPC2_CODE)),
                    annenFravarsArsak =
                        AnnenFravarsArsak(
                            grunn = listOf("Annen årsak"),
                            beskrivelse = "Beskrivelse av annen årsak",
                        ),
                    aktivitet =
                        listOf(
                            Aktivitet.IkkeMulig(
                                fom = LocalDate.now().minusDays(10),
                                tom = LocalDate.now().plusDays(10),
                            )
                        ),
                    utdypendeOpplysninger =
                        mapOf(
                            "6.5" to
                                mapOf(
                                    "6.5.1" to mapOf("tekst" to "Svar på spørsmål 1"),
                                    "6.5.2" to mapOf("tekst" to "Svar på spørsmål 2"),
                                    "6.5.3" to mapOf("tekst" to "Svar på spørsmål 3"),
                                    "6.5.4" to mapOf("tekst" to "Svar på spørsmål 4"),
                                )
                        ),
                    tidligereSykmeldinger =
                        listOf(
                            TidligereSykmelding(
                                sykmeldingId = "987654321",
                                aktivitet =
                                    listOf(
                                        TidligereSykmeldingAktivitet.IkkeMulig(
                                            fom = LocalDate.now().minusDays(30),
                                            tom = LocalDate.now().minusDays(11),
                                        )
                                    ),
                                hoveddiagnose = Diagnose(kode = "A01", system = "ICD-10"),
                                meta =
                                    TidligereSykmeldingMeta(
                                        status = RegulaStatus.OK,
                                        userAction = "SENDT",
                                        merknader = null,
                                    ),
                            )
                        ),
                    kontaktPasientBegrunnelseIkkeKontakt = "Pasienten var ikke tilgjengelig",
                    pasient =
                        RegulaPasient(
                            ident = "12345678910",
                            fodselsdato = LocalDate.now().minusYears(30),
                        ),
                    behandletTidspunkt = LocalDateTime.now(),
                    meta =
                        RegulaMeta.LegacyMeta(
                            signaturdato = LocalDateTime.now().minusDays(1),
                            mottattDato = LocalDateTime.now(),
                            rulesetVersion = "2",
                        ),
                    behandler =
                        RegulaBehandler.Finnes(
                            suspendert = false,
                            godkjenninger =
                                listOf(
                                    BehandlerGodkjenning(
                                        autorisasjon =
                                            BehandlerKode(aktiv = true, oid = 7704, verdi = "1"),
                                        helsepersonellkategori =
                                            BehandlerKode(aktiv = true, oid = 9060, verdi = "LE"),
                                        tillegskompetanse = null,
                                    )
                                ),
                            legekontorOrgnr = "123456789",
                            fnr = "10987654321",
                        ),
                    avsender = RegulaAvsender.Finnes(fnr = "10987654321"),
                ),
                ExecutionMode.NORMAL,
            )

        assertIs<RegulaResult.Ok>(result)
        assertEquals(result.status, RegulaStatus.OK)

        // All 8 chains
        assertEquals(result.results.size, 8)
    }

    @Test
    fun `smallest possible valid sykmelding`() {
        val result =
            executeRegulaRules(
                RegulaPayload(
                    sykmeldingId = "123456789",
                    hoveddiagnose = Diagnose(kode = "A01", system = Diagnosekoder.ICPC2_CODE),
                    aktivitet =
                        listOf(
                            Aktivitet.IkkeMulig(
                                fom = LocalDate.now().minusDays(0),
                                tom = LocalDate.now().plusDays(7),
                            )
                        ),
                    behandletTidspunkt = LocalDateTime.now(),
                    meta =
                        RegulaMeta.LegacyMeta(
                            signaturdato = LocalDateTime.now().minusDays(1),
                            mottattDato = LocalDateTime.now(),
                            rulesetVersion = "2",
                        ),
                    tidligereSykmeldinger = emptyList(),
                    bidiagnoser = null,
                    annenFravarsArsak = null,
                    utdypendeOpplysninger = null,
                    kontaktPasientBegrunnelseIkkeKontakt = null,
                    pasient =
                        RegulaPasient(
                            ident = "12345678910",
                            fodselsdato = LocalDate.now().minusYears(30),
                        ),
                    behandler =
                        RegulaBehandler.Finnes(
                            suspendert = false,
                            godkjenninger =
                                listOf(
                                    BehandlerGodkjenning(
                                        autorisasjon =
                                            BehandlerKode(aktiv = true, oid = 7704, verdi = "1"),
                                        helsepersonellkategori =
                                            BehandlerKode(aktiv = true, oid = 9060, verdi = "LE"),
                                        tillegskompetanse = null,
                                    )
                                ),
                            legekontorOrgnr = "123456789",
                            fnr = "10987654321",
                        ),
                    avsender = RegulaAvsender.Finnes(fnr = "10987654321"),
                ),
                ExecutionMode.NORMAL,
            )

        assertIs<RegulaResult.Ok>(result)
        assertEquals(result.status, RegulaStatus.OK)

        // All 8 chains
        assertEquals(result.results.size, 8)
    }

    @Test
    fun `should support having no avsender`() {
        val result =
            executeRegulaRules(
                RegulaPayload(
                    sykmeldingId = "123456789",
                    hoveddiagnose = Diagnose(kode = "A01", system = Diagnosekoder.ICPC2_CODE),
                    aktivitet =
                        listOf(
                            Aktivitet.IkkeMulig(
                                fom = LocalDate.now().minusDays(0),
                                tom = LocalDate.now().plusDays(7),
                            )
                        ),
                    behandletTidspunkt = LocalDateTime.now(),
                    meta =
                        RegulaMeta.LegacyMeta(
                            signaturdato = LocalDateTime.now().minusDays(1),
                            mottattDato = LocalDateTime.now(),
                            rulesetVersion = "2",
                        ),
                    tidligereSykmeldinger = emptyList(),
                    bidiagnoser = null,
                    annenFravarsArsak = null,
                    utdypendeOpplysninger = null,
                    kontaktPasientBegrunnelseIkkeKontakt = null,
                    pasient =
                        RegulaPasient(
                            ident = "12345678910",
                            fodselsdato = LocalDate.now().minusYears(30),
                        ),
                    behandler =
                        RegulaBehandler.Finnes(
                            suspendert = false,
                            godkjenninger =
                                listOf(
                                    BehandlerGodkjenning(
                                        autorisasjon =
                                            BehandlerKode(aktiv = true, oid = 7704, verdi = "1"),
                                        helsepersonellkategori =
                                            BehandlerKode(aktiv = true, oid = 9060, verdi = "LE"),
                                        tillegskompetanse = null,
                                    )
                                ),
                            legekontorOrgnr = "123456789",
                            fnr = "10987654321",
                        ),
                    avsender = RegulaAvsender.IngenAvsender,
                ),
                ExecutionMode.PAPIR,
            )

        assertIs<RegulaResult.Ok>(result)
        assertEquals(result.status, RegulaStatus.OK)

        // All 8 chains
        assertEquals(result.results.size, 8)
    }

    @Test
    fun `should give the name of the outcome based on the enum named`() {
        val payload =
            RegulaPayload(
                sykmeldingId = "123456789",
                hoveddiagnose = null,
                bidiagnoser = null,
                annenFravarsArsak = null,
                aktivitet =
                    listOf(
                        Aktivitet.IkkeMulig(
                            fom = LocalDate.now().minusDays(10),
                            tom = LocalDate.now().plusDays(10),
                        )
                    ),
                utdypendeOpplysninger = null,
                tidligereSykmeldinger = emptyList(),
                kontaktPasientBegrunnelseIkkeKontakt = null,
                pasient =
                    RegulaPasient(
                        ident = "12345678910",
                        fodselsdato = LocalDate.now().minusYears(30),
                    ),
                behandletTidspunkt = LocalDateTime.now(),
                meta =
                    RegulaMeta.LegacyMeta(
                        signaturdato = LocalDateTime.now().minusDays(1),
                        mottattDato = LocalDateTime.now(),
                        rulesetVersion = "2",
                    ),
                behandler = RegulaBehandler.FinnesIkke(fnr = "10987654321"),
                avsender = RegulaAvsender.IngenAvsender,
            )

        val result = executeRegulaRules(payload, ExecutionMode.NORMAL)

        assertIs<RegulaResult.NotOk>(result)
        assertEquals(result.status, RegulaStatus.INVALID)
        assertEquals(result.outcome.status, RegulaOutcomeStatus.INVALID)

        assertEquals(result.results.size, 4)
        assertEquals(result.outcome.rule, "BEHANDLER_IKKE_I_HPR")
        assertEquals(result.outcome.tree, "Behandler i HPR")
        assertEquals(
            result.outcome.reason.sykmeldt,
            "Avsender fodselsnummer er ikke registert i Helsepersonellregisteret (HPR)",
        )
    }
}
