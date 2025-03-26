package no.nav.tsm.regulus.regula.trees.hpr

import java.time.LocalDate
import java.time.LocalDateTime
import kotlin.test.Test
import kotlin.test.assertEquals
import no.nav.tsm.regulus.regula.executor.RuleStatus
import no.nav.tsm.regulus.regula.payload.SykmeldingPeriode
import no.nav.tsm.regulus.regula.trees.assertPath

class HprRulesTest {
    private fun createHprRulePayload(
        behandler: Behandler,
        perioder: List<SykmeldingPeriode> = emptyList(),
        startdato: LocalDate? = null,
        signaturdato: LocalDateTime = LocalDateTime.now(),
    ) =
        HprRulePayload(
            sykmeldingId = "test-sykmelding-id",
            behandler = behandler,
            perioder = perioder,
            startdato = startdato,
            signaturdato = signaturdato,
        )

    @Test
    fun `har ikke aktiv autorisasjon, Status INVALID`() {
        val behandler = testBehandler(BehandlerScenarios.INAKTIV_LEGE)
        val (result) = HprRules(createHprRulePayload(behandler)).execute()

        assertEquals(RuleStatus.INVALID, result.treeResult.status)
        assertPath(result.rulePath, listOf(HprRule.BEHANDLER_GYLIDG_I_HPR to false))
        assertEquals(mapOf("behandlerGodkjenninger" to behandler.godkjenninger), result.ruleInputs)
    }

    @Test
    fun `mangler autorisasjon, Status INVALID`() {
        val behandler = testBehandler(BehandlerScenarios.UGYLDIG_AUTORISASJON)
        val (result) = HprRules(createHprRulePayload(behandler)).execute()

        assertEquals(RuleStatus.INVALID, result.treeResult.status)
        assertPath(
            result.rulePath,
            listOf(
                HprRule.BEHANDLER_GYLIDG_I_HPR to true,
                HprRule.BEHANDLER_HAR_AUTORISASJON_I_HPR to false,
            ),
        )
        assertEquals(mapOf("behandlerGodkjenninger" to behandler.godkjenninger), result.ruleInputs)
    }

    @Test
    fun `LEGE har aktiv autorisasjon, Status OK`() {
        val behandler = testBehandler(BehandlerScenarios.AKTIV_LEGE)
        val perioder =
            listOf(
                SykmeldingPeriode.AktivitetIkkeMulig(
                    LocalDate.of(2020, 1, 1),
                    LocalDate.of(2020, 1, 2),
                )
            )
        val signaturdato = LocalDate.of(2020, 1, 3).atStartOfDay()
        val (result) =
            HprRules(createHprRulePayload(behandler, perioder, signaturdato = signaturdato))
                .execute()

        assertEquals(RuleStatus.OK, result.treeResult.status)
        assertPath(
            result.rulePath,
            listOf(
                HprRule.BEHANDLER_GYLIDG_I_HPR to true,
                HprRule.BEHANDLER_HAR_AUTORISASJON_I_HPR to true,
                HprRule.BEHANDLER_ER_LEGE_I_HPR to true,
            ),
        )
        assertEquals(mapOf("behandlerGodkjenninger" to behandler.godkjenninger), result.ruleInputs)
    }

    @Test
    fun `Manuellterapeut har aktiv autorisasjon, Status OK`() {
        val behandler = testBehandler(BehandlerScenarios.AKTIV_MANUELLTERAPEUT)
        val perioder =
            listOf(
                SykmeldingPeriode.AktivitetIkkeMulig(
                    LocalDate.of(2020, 1, 1),
                    LocalDate.of(2020, 1, 2),
                )
            )
        val signaturdato = LocalDate.of(2020, 1, 3).atStartOfDay()
        val (result) =
            HprRules(createHprRulePayload(behandler, perioder, signaturdato = signaturdato))
                .execute()

        assertEquals(RuleStatus.OK, result.treeResult.status)
        assertPath(
            result.rulePath,
            listOf(
                HprRule.BEHANDLER_GYLIDG_I_HPR to true,
                HprRule.BEHANDLER_HAR_AUTORISASJON_I_HPR to true,
                HprRule.BEHANDLER_ER_LEGE_I_HPR to false,
                HprRule.BEHANDLER_ER_TANNLEGE_I_HPR to false,
                HprRule.BEHANDLER_ER_MANUELLTERAPEUT_I_HPR to true,
                HprRule.SYKEFRAVAR_OVER_12_UKER to false,
            ),
        )
        assertEquals(
            mapOf(
                "behandlerGodkjenninger" to behandler.godkjenninger,
                "fom" to perioder.first().fom,
                "tom" to perioder.first().tom,
                "startDatoSykefravær" to perioder.first().fom,
            ),
            result.ruleInputs,
        )
    }

    @Test
    fun `TANNLEGE har aktiv autorisasjon, Status OK`() {
        val behandler = testBehandler(BehandlerScenarios.AKTIV_TANNLEGE)
        val perioder =
            listOf(
                SykmeldingPeriode.AktivitetIkkeMulig(
                    LocalDate.of(2020, 1, 1),
                    LocalDate.of(2020, 1, 2),
                )
            )
        val signaturdato = LocalDate.of(2020, 1, 3).atStartOfDay()
        val (result) =
            HprRules(createHprRulePayload(behandler, perioder, signaturdato = signaturdato))
                .execute()

        assertEquals(RuleStatus.OK, result.treeResult.status)
        assertPath(
            result.rulePath,
            listOf(
                HprRule.BEHANDLER_GYLIDG_I_HPR to true,
                HprRule.BEHANDLER_HAR_AUTORISASJON_I_HPR to true,
                HprRule.BEHANDLER_ER_LEGE_I_HPR to false,
                HprRule.BEHANDLER_ER_TANNLEGE_I_HPR to true,
            ),
        )
        assertEquals(mapOf("behandlerGodkjenninger" to behandler.godkjenninger), result.ruleInputs)
    }

    @Test
    fun `Manuellterapeut har aktiv autorisasjon, sykefravær over 12 uker, Status INVALID`() {
        val behandler = testBehandler(BehandlerScenarios.AKTIV_MANUELLTERAPEUT)
        val perioder =
            listOf(
                SykmeldingPeriode.AktivitetIkkeMulig(
                    LocalDate.of(2020, 1, 1),
                    LocalDate.of(2020, 1, 2),
                )
            )
        val startdato =
            LocalDate.of(2020, 1, 2).minusDays(85) // More than 12 weeks (84 days) before tom date
        val signaturdato = LocalDate.of(2020, 1, 3).atStartOfDay()
        val (result) =
            HprRules(createHprRulePayload(behandler, perioder, startdato, signaturdato)).execute()

        assertEquals(RuleStatus.INVALID, result.treeResult.status)
        assertPath(
            result.rulePath,
            listOf(
                HprRule.BEHANDLER_GYLIDG_I_HPR to true,
                HprRule.BEHANDLER_HAR_AUTORISASJON_I_HPR to true,
                HprRule.BEHANDLER_ER_LEGE_I_HPR to false,
                HprRule.BEHANDLER_ER_TANNLEGE_I_HPR to false,
                HprRule.BEHANDLER_ER_MANUELLTERAPEUT_I_HPR to true,
                HprRule.SYKEFRAVAR_OVER_12_UKER to true,
            ),
        )
        assertEquals(
            mapOf(
                "behandlerGodkjenninger" to behandler.godkjenninger,
                "fom" to perioder.first().fom,
                "tom" to perioder.first().tom,
                "startDatoSykefravær" to startdato,
            ),
            result.ruleInputs,
        )
    }

    @Test
    fun `Fysioterapeut uten tilleggskompetanse, Status INVALID`() {
        val behandler = testBehandler(BehandlerScenarios.AKTIV_FYSIOTERAPEUT)
        val perioder =
            listOf(
                SykmeldingPeriode.AktivitetIkkeMulig(
                    LocalDate.of(2020, 1, 1),
                    LocalDate.of(2020, 4, 2),
                )
            )
        val signaturdato = LocalDate.of(2020, 1, 3).atStartOfDay()
        val (result) =
            HprRules(createHprRulePayload(behandler, perioder, null, signaturdato)).execute()

        assertEquals(RuleStatus.INVALID, result.treeResult.status)
        assertPath(
            result.rulePath,
            listOf(
                HprRule.BEHANDLER_GYLIDG_I_HPR to true,
                HprRule.BEHANDLER_HAR_AUTORISASJON_I_HPR to true,
                HprRule.BEHANDLER_ER_LEGE_I_HPR to false,
                HprRule.BEHANDLER_ER_TANNLEGE_I_HPR to false,
                HprRule.BEHANDLER_ER_MANUELLTERAPEUT_I_HPR to false,
                HprRule.BEHANDLER_ER_FT_MED_TILLEGSKOMPETANSE_I_HPR to false,
                HprRule.BEHANDLER_ER_KI_MED_TILLEGSKOMPETANSE_I_HPR to false,
            ),
        )
        assertEquals(
            mapOf(
                "behandlerGodkjenninger" to behandler.godkjenninger,
                "genereringsTidspunkt" to signaturdato,
            ),
            result.ruleInputs,
        )
    }

    @Test
    fun `Fysioterapeut med tilleggskompetanse, Status OK`() {
        val behandler = testBehandler(BehandlerScenarios.AKTIV_FYSIOTERAPEUT_MED_TILLEGGSKOMPETANSE)
        val perioder =
            listOf(
                SykmeldingPeriode.AktivitetIkkeMulig(
                    LocalDate.of(2020, 1, 1),
                    LocalDate.of(2020, 1, 2),
                )
            )
        val signaturdato = LocalDate.of(2020, 1, 3).atStartOfDay()
        val (result) =
            HprRules(createHprRulePayload(behandler, perioder, null, signaturdato)).execute()

        assertEquals(RuleStatus.OK, result.treeResult.status)
        assertPath(
            result.rulePath,
            listOf(
                HprRule.BEHANDLER_GYLIDG_I_HPR to true,
                HprRule.BEHANDLER_HAR_AUTORISASJON_I_HPR to true,
                HprRule.BEHANDLER_ER_LEGE_I_HPR to false,
                HprRule.BEHANDLER_ER_TANNLEGE_I_HPR to false,
                HprRule.BEHANDLER_ER_MANUELLTERAPEUT_I_HPR to false,
                HprRule.BEHANDLER_ER_FT_MED_TILLEGSKOMPETANSE_I_HPR to true,
                HprRule.SYKEFRAVAR_OVER_12_UKER to false,
            ),
        )
    }

    @Test
    fun `Fysioterapeut med tilleggskompetanse, sykefravær over 12 uker, Status INVALID`() {
        val behandler = testBehandler(BehandlerScenarios.AKTIV_FYSIOTERAPEUT_MED_TILLEGGSKOMPETANSE)
        val perioder =
            listOf(
                SykmeldingPeriode.AktivitetIkkeMulig(
                    LocalDate.of(2020, 1, 1),
                    LocalDate.of(2020, 1, 2),
                )
            )
        val startdato =
            LocalDate.of(2020, 1, 2).minusDays(85) // More than 12 weeks (84 days) before tom date
        val signaturdato = LocalDate.of(2020, 1, 3).atStartOfDay()
        val (result) =
            HprRules(createHprRulePayload(behandler, perioder, startdato, signaturdato)).execute()

        assertEquals(RuleStatus.INVALID, result.treeResult.status)
        assertPath(
            result.rulePath,
            listOf(
                HprRule.BEHANDLER_GYLIDG_I_HPR to true,
                HprRule.BEHANDLER_HAR_AUTORISASJON_I_HPR to true,
                HprRule.BEHANDLER_ER_LEGE_I_HPR to false,
                HprRule.BEHANDLER_ER_TANNLEGE_I_HPR to false,
                HprRule.BEHANDLER_ER_MANUELLTERAPEUT_I_HPR to false,
                HprRule.BEHANDLER_ER_FT_MED_TILLEGSKOMPETANSE_I_HPR to true,
                HprRule.SYKEFRAVAR_OVER_12_UKER to true,
            ),
        )
        assertEquals(
            mapOf(
                "behandlerGodkjenninger" to behandler.godkjenninger,
                "genereringsTidspunkt" to signaturdato,
                "fom" to perioder.first().fom,
                "tom" to perioder.first().tom,
                "startDatoSykefravær" to startdato,
            ),
            result.ruleInputs,
        )
    }

    @Test
    fun `Kiropraktor uten tilleggskompetanse, Status INVALID`() {
        val behandler = testBehandler(BehandlerScenarios.AKTIV_KIROPRAKTOR)
        val perioder =
            listOf(
                SykmeldingPeriode.AktivitetIkkeMulig(
                    LocalDate.of(2020, 1, 1),
                    LocalDate.of(2020, 4, 2),
                )
            )
        val signaturdato = LocalDate.of(2020, 1, 3).atStartOfDay()
        val (result) =
            HprRules(createHprRulePayload(behandler, perioder, null, signaturdato)).execute()

        assertEquals(RuleStatus.INVALID, result.treeResult.status)
        assertPath(
            result.rulePath,
            listOf(
                HprRule.BEHANDLER_GYLIDG_I_HPR to true,
                HprRule.BEHANDLER_HAR_AUTORISASJON_I_HPR to true,
                HprRule.BEHANDLER_ER_LEGE_I_HPR to false,
                HprRule.BEHANDLER_ER_TANNLEGE_I_HPR to false,
                HprRule.BEHANDLER_ER_MANUELLTERAPEUT_I_HPR to false,
                HprRule.BEHANDLER_ER_FT_MED_TILLEGSKOMPETANSE_I_HPR to false,
                HprRule.BEHANDLER_ER_KI_MED_TILLEGSKOMPETANSE_I_HPR to false,
            ),
        )
        assertEquals(
            mapOf(
                "behandlerGodkjenninger" to behandler.godkjenninger,
                "genereringsTidspunkt" to signaturdato,
            ),
            result.ruleInputs,
        )
    }

    @Test
    fun `Kiropraktor med tilleggskompetanse, Status OK`() {
        val behandler = testBehandler(BehandlerScenarios.AKTIV_KIROPRAKTOR_MED_TILLEGGSKOMPETANSE)
        val perioder =
            listOf(
                SykmeldingPeriode.AktivitetIkkeMulig(
                    LocalDate.of(2020, 1, 1),
                    LocalDate.of(2020, 1, 2),
                )
            )
        val signaturdato = LocalDate.of(2020, 1, 3).atStartOfDay()
        val (result) =
            HprRules(createHprRulePayload(behandler, perioder, null, signaturdato)).execute()

        assertEquals(RuleStatus.OK, result.treeResult.status)
        assertPath(
            result.rulePath,
            listOf(
                HprRule.BEHANDLER_GYLIDG_I_HPR to true,
                HprRule.BEHANDLER_HAR_AUTORISASJON_I_HPR to true,
                HprRule.BEHANDLER_ER_LEGE_I_HPR to false,
                HprRule.BEHANDLER_ER_TANNLEGE_I_HPR to false,
                HprRule.BEHANDLER_ER_MANUELLTERAPEUT_I_HPR to false,
                HprRule.BEHANDLER_ER_FT_MED_TILLEGSKOMPETANSE_I_HPR to false,
                HprRule.BEHANDLER_ER_KI_MED_TILLEGSKOMPETANSE_I_HPR to true,
                HprRule.SYKEFRAVAR_OVER_12_UKER to false,
            ),
        )
    }

    @Test
    fun `Kiropraktor med tilleggskompetanse, sykefravær over 12 uker, Status INVALID`() {
        val behandler = testBehandler(BehandlerScenarios.AKTIV_KIROPRAKTOR_MED_TILLEGGSKOMPETANSE)
        val perioder =
            listOf(
                SykmeldingPeriode.AktivitetIkkeMulig(
                    LocalDate.of(2020, 1, 1),
                    LocalDate.of(2020, 1, 2),
                )
            )
        val startdato =
            LocalDate.of(2020, 1, 2).minusDays(85) // More than 12 weeks (84 days) before tom date
        val signaturdato = LocalDate.of(2020, 1, 3).atStartOfDay()
        val (result) =
            HprRules(createHprRulePayload(behandler, perioder, startdato, signaturdato)).execute()

        assertEquals(RuleStatus.INVALID, result.treeResult.status)
        assertPath(
            result.rulePath,
            listOf(
                HprRule.BEHANDLER_GYLIDG_I_HPR to true,
                HprRule.BEHANDLER_HAR_AUTORISASJON_I_HPR to true,
                HprRule.BEHANDLER_ER_LEGE_I_HPR to false,
                HprRule.BEHANDLER_ER_TANNLEGE_I_HPR to false,
                HprRule.BEHANDLER_ER_MANUELLTERAPEUT_I_HPR to false,
                HprRule.BEHANDLER_ER_FT_MED_TILLEGSKOMPETANSE_I_HPR to false,
                HprRule.BEHANDLER_ER_KI_MED_TILLEGSKOMPETANSE_I_HPR to true,
                HprRule.SYKEFRAVAR_OVER_12_UKER to true,
            ),
        )
        assertEquals(
            mapOf(
                "behandlerGodkjenninger" to behandler.godkjenninger,
                "genereringsTidspunkt" to signaturdato,
                "fom" to perioder.first().fom,
                "tom" to perioder.first().tom,
                "startDatoSykefravær" to startdato,
            ),
            result.ruleInputs,
        )
    }

    @Test
    fun `Fysioterapeut med tilleggskompetanse, gyldig periode fra samme dag som genereringstidspunkt, Status OK`() {
        val signaturdato = LocalDate.of(2020, 1, 3).atStartOfDay()
        val behandler = testBehandler(BehandlerScenarios.AKTIV_FYSIOTERAPEUT_MED_TILLEGGSKOMPETANSE)

        // Override the gyldig.fra to be the same as signaturdato
        val behandlerWithCustomGyldig =
            Behandler(
                godkjenninger =
                    listOf(
                        Godkjenning(
                            autorisasjon = Kode(aktiv = true, oid = 7704, verdi = "1"),
                            helsepersonellkategori = Kode(aktiv = true, oid = 0, verdi = "FT"),
                            tillegskompetanse =
                                listOf(
                                    Tilleggskompetanse(
                                        avsluttetStatus = null,
                                        eTag = null,
                                        gyldig = Periode(fra = signaturdato, til = null),
                                        id = null,
                                        type = Kode(aktiv = true, oid = 7702, verdi = "1"),
                                    )
                                ),
                        )
                    )
            )

        val perioder =
            listOf(
                SykmeldingPeriode.AktivitetIkkeMulig(
                    LocalDate.of(2020, 1, 1),
                    LocalDate.of(2020, 1, 2),
                )
            )
        val (result) =
            HprRules(createHprRulePayload(behandlerWithCustomGyldig, perioder, null, signaturdato))
                .execute()

        assertEquals(RuleStatus.OK, result.treeResult.status)
        assertPath(
            result.rulePath,
            listOf(
                HprRule.BEHANDLER_GYLIDG_I_HPR to true,
                HprRule.BEHANDLER_HAR_AUTORISASJON_I_HPR to true,
                HprRule.BEHANDLER_ER_LEGE_I_HPR to false,
                HprRule.BEHANDLER_ER_TANNLEGE_I_HPR to false,
                HprRule.BEHANDLER_ER_MANUELLTERAPEUT_I_HPR to false,
                HprRule.BEHANDLER_ER_FT_MED_TILLEGSKOMPETANSE_I_HPR to true,
                HprRule.SYKEFRAVAR_OVER_12_UKER to false,
            ),
        )
    }

    @Test
    fun `Fysioterapeut med tilleggskompetanse, ugyldig periode, Status INVALID`() {
        val signaturdato = LocalDate.of(2020, 1, 3).atStartOfDay()
        val behandler = testBehandler(BehandlerScenarios.AKTIV_FYSIOTERAPEUT_MED_TILLEGGSKOMPETANSE)

        // Override the gyldig.fra to be one day after signaturdato
        val behandlerWithCustomGyldig =
            Behandler(
                godkjenninger =
                    listOf(
                        Godkjenning(
                            autorisasjon = Kode(aktiv = true, oid = 7704, verdi = "1"),
                            helsepersonellkategori = Kode(aktiv = true, oid = 0, verdi = "FT"),
                            tillegskompetanse =
                                listOf(
                                    Tilleggskompetanse(
                                        avsluttetStatus = null,
                                        eTag = null,
                                        gyldig =
                                            Periode(fra = signaturdato.plusDays(1), til = null),
                                        id = null,
                                        type = Kode(aktiv = true, oid = 7702, verdi = "1"),
                                    )
                                ),
                        )
                    )
            )

        val perioder =
            listOf(
                SykmeldingPeriode.AktivitetIkkeMulig(
                    LocalDate.of(2020, 1, 1),
                    LocalDate.of(2020, 1, 2),
                )
            )
        val (result) =
            HprRules(createHprRulePayload(behandlerWithCustomGyldig, perioder, null, signaturdato))
                .execute()

        assertEquals(RuleStatus.INVALID, result.treeResult.status)
        assertPath(
            result.rulePath,
            listOf(
                HprRule.BEHANDLER_GYLIDG_I_HPR to true,
                HprRule.BEHANDLER_HAR_AUTORISASJON_I_HPR to true,
                HprRule.BEHANDLER_ER_LEGE_I_HPR to false,
                HprRule.BEHANDLER_ER_TANNLEGE_I_HPR to false,
                HprRule.BEHANDLER_ER_MANUELLTERAPEUT_I_HPR to false,
                HprRule.BEHANDLER_ER_FT_MED_TILLEGSKOMPETANSE_I_HPR to false,
                HprRule.BEHANDLER_ER_KI_MED_TILLEGSKOMPETANSE_I_HPR to false,
            ),
        )
        assertEquals(
            mapOf(
                "behandlerGodkjenninger" to behandlerWithCustomGyldig.godkjenninger,
                "genereringsTidspunkt" to signaturdato,
            ),
            result.ruleInputs,
        )
    }

    @Test
    fun `Fysioterapeut med tilleggskompetanse, ugyldig periode tom før genereringstidspunkt, Status INVALID`() {
        val signaturdato = LocalDate.of(2020, 1, 3).atStartOfDay()
        val behandler = testBehandler(BehandlerScenarios.AKTIV_FYSIOTERAPEUT_MED_TILLEGGSKOMPETANSE)

        // Override the gyldig.til to be one day before signaturdato
        val behandlerWithCustomGyldig =
            Behandler(
                godkjenninger =
                    listOf(
                        Godkjenning(
                            autorisasjon = Kode(aktiv = true, oid = 7704, verdi = "1"),
                            helsepersonellkategori = Kode(aktiv = true, oid = 0, verdi = "FT"),
                            tillegskompetanse =
                                listOf(
                                    Tilleggskompetanse(
                                        avsluttetStatus = null,
                                        eTag = null,
                                        gyldig =
                                            Periode(
                                                fra = signaturdato.minusDays(10),
                                                til = signaturdato.minusDays(1),
                                            ),
                                        id = null,
                                        type = Kode(aktiv = true, oid = 7702, verdi = "1"),
                                    )
                                ),
                        )
                    )
            )

        val perioder =
            listOf(
                SykmeldingPeriode.AktivitetIkkeMulig(
                    LocalDate.of(2020, 1, 1),
                    LocalDate.of(2020, 1, 2),
                )
            )
        val (result) =
            HprRules(createHprRulePayload(behandlerWithCustomGyldig, perioder, null, signaturdato))
                .execute()

        assertEquals(RuleStatus.INVALID, result.treeResult.status)
        assertPath(
            result.rulePath,
            listOf(
                HprRule.BEHANDLER_GYLIDG_I_HPR to true,
                HprRule.BEHANDLER_HAR_AUTORISASJON_I_HPR to true,
                HprRule.BEHANDLER_ER_LEGE_I_HPR to false,
                HprRule.BEHANDLER_ER_TANNLEGE_I_HPR to false,
                HprRule.BEHANDLER_ER_MANUELLTERAPEUT_I_HPR to false,
                HprRule.BEHANDLER_ER_FT_MED_TILLEGSKOMPETANSE_I_HPR to false,
                HprRule.BEHANDLER_ER_KI_MED_TILLEGSKOMPETANSE_I_HPR to false,
            ),
        )
        assertEquals(
            mapOf(
                "behandlerGodkjenninger" to behandlerWithCustomGyldig.godkjenninger,
                "genereringsTidspunkt" to signaturdato,
            ),
            result.ruleInputs,
        )
    }

    @Test
    fun `Fysioterapeut med tilleggskompetanse, gyldig periode tom samme som genereringstidspunkt, Status OK`() {
        val signaturdato = LocalDate.of(2020, 1, 3).atStartOfDay()
        val behandler = testBehandler(BehandlerScenarios.AKTIV_FYSIOTERAPEUT_MED_TILLEGGSKOMPETANSE)

        // Override the gyldig.til to be the same as signaturdato
        val behandlerWithCustomGyldig =
            Behandler(
                godkjenninger =
                    listOf(
                        Godkjenning(
                            autorisasjon = Kode(aktiv = true, oid = 7704, verdi = "1"),
                            helsepersonellkategori = Kode(aktiv = true, oid = 0, verdi = "FT"),
                            tillegskompetanse =
                                listOf(
                                    Tilleggskompetanse(
                                        avsluttetStatus = null,
                                        eTag = null,
                                        gyldig =
                                            Periode(
                                                fra = signaturdato.minusDays(10),
                                                til = signaturdato,
                                            ),
                                        id = null,
                                        type = Kode(aktiv = true, oid = 7702, verdi = "1"),
                                    )
                                ),
                        )
                    )
            )

        val perioder =
            listOf(
                SykmeldingPeriode.AktivitetIkkeMulig(
                    LocalDate.of(2020, 1, 1),
                    LocalDate.of(2020, 1, 2),
                )
            )
        val (result) =
            HprRules(createHprRulePayload(behandlerWithCustomGyldig, perioder, null, signaturdato))
                .execute()

        assertEquals(RuleStatus.OK, result.treeResult.status)
        assertPath(
            result.rulePath,
            listOf(
                HprRule.BEHANDLER_GYLIDG_I_HPR to true,
                HprRule.BEHANDLER_HAR_AUTORISASJON_I_HPR to true,
                HprRule.BEHANDLER_ER_LEGE_I_HPR to false,
                HprRule.BEHANDLER_ER_TANNLEGE_I_HPR to false,
                HprRule.BEHANDLER_ER_MANUELLTERAPEUT_I_HPR to false,
                HprRule.BEHANDLER_ER_FT_MED_TILLEGSKOMPETANSE_I_HPR to true,
                HprRule.SYKEFRAVAR_OVER_12_UKER to false,
            ),
        )
    }

    @Test
    fun `Fysioterapeut med feil tilleggskompetanse type verdi, Status INVALID`() {
        val behandler =
            testBehandler(BehandlerScenarios.AKTIV_FYSIOTERAPEUT_MED_FEIL_TILLEGGSKOMPETANSE_TYPE)
        val perioder =
            listOf(
                SykmeldingPeriode.AktivitetIkkeMulig(
                    LocalDate.of(2020, 1, 1),
                    LocalDate.of(2020, 1, 2),
                )
            )
        val signaturdato = LocalDate.of(2020, 1, 3).atStartOfDay()
        val (result) =
            HprRules(createHprRulePayload(behandler, perioder, null, signaturdato)).execute()

        assertEquals(RuleStatus.INVALID, result.treeResult.status)
        assertPath(
            result.rulePath,
            listOf(
                HprRule.BEHANDLER_GYLIDG_I_HPR to true,
                HprRule.BEHANDLER_HAR_AUTORISASJON_I_HPR to true,
                HprRule.BEHANDLER_ER_LEGE_I_HPR to false,
                HprRule.BEHANDLER_ER_TANNLEGE_I_HPR to false,
                HprRule.BEHANDLER_ER_MANUELLTERAPEUT_I_HPR to false,
                HprRule.BEHANDLER_ER_FT_MED_TILLEGSKOMPETANSE_I_HPR to false,
                HprRule.BEHANDLER_ER_KI_MED_TILLEGSKOMPETANSE_I_HPR to false,
            ),
        )
        assertEquals(
            mapOf(
                "behandlerGodkjenninger" to behandler.godkjenninger,
                "genereringsTidspunkt" to signaturdato,
            ),
            result.ruleInputs,
        )
    }

    @Test
    fun `Fysioterapeut med inaktiv tilleggskompetanse, Status INVALID`() {
        val behandler =
            testBehandler(BehandlerScenarios.AKTIV_FYSIOTERAPEUT_MED_INAKTIV_TILLEGGSKOMPETANSE)
        val perioder =
            listOf(
                SykmeldingPeriode.AktivitetIkkeMulig(
                    LocalDate.of(2020, 1, 1),
                    LocalDate.of(2020, 1, 2),
                )
            )
        val signaturdato = LocalDate.of(2020, 1, 3).atStartOfDay()
        val (result) =
            HprRules(createHprRulePayload(behandler, perioder, null, signaturdato)).execute()

        assertEquals(RuleStatus.INVALID, result.treeResult.status)
        assertPath(
            result.rulePath,
            listOf(
                HprRule.BEHANDLER_GYLIDG_I_HPR to true,
                HprRule.BEHANDLER_HAR_AUTORISASJON_I_HPR to true,
                HprRule.BEHANDLER_ER_LEGE_I_HPR to false,
                HprRule.BEHANDLER_ER_TANNLEGE_I_HPR to false,
                HprRule.BEHANDLER_ER_MANUELLTERAPEUT_I_HPR to false,
                HprRule.BEHANDLER_ER_FT_MED_TILLEGSKOMPETANSE_I_HPR to false,
                HprRule.BEHANDLER_ER_KI_MED_TILLEGSKOMPETANSE_I_HPR to false,
            ),
        )
        assertEquals(
            mapOf(
                "behandlerGodkjenninger" to behandler.godkjenninger,
                "genereringsTidspunkt" to signaturdato,
            ),
            result.ruleInputs,
        )
    }

    @Test
    fun `Kiropraktor med tilleggskompetanse og annen helsepersonellkategori, Status OK`() {
        val behandler = testBehandler(BehandlerScenarios.AKTIV_KIROPRAKTOR_MED_TILLEGGSKOMPETANSE)
        val perioder =
            listOf(
                SykmeldingPeriode.AktivitetIkkeMulig(
                    LocalDate.of(2020, 1, 1),
                    LocalDate.of(2020, 1, 2),
                )
            )
        val signaturdato = LocalDate.of(2020, 1, 3).atStartOfDay()
        val (result) =
            HprRules(createHprRulePayload(behandler, perioder, null, signaturdato)).execute()

        assertEquals(RuleStatus.OK, result.treeResult.status)
        assertPath(
            result.rulePath,
            listOf(
                HprRule.BEHANDLER_GYLIDG_I_HPR to true,
                HprRule.BEHANDLER_HAR_AUTORISASJON_I_HPR to true,
                HprRule.BEHANDLER_ER_LEGE_I_HPR to false,
                HprRule.BEHANDLER_ER_TANNLEGE_I_HPR to false,
                HprRule.BEHANDLER_ER_MANUELLTERAPEUT_I_HPR to false,
                HprRule.BEHANDLER_ER_FT_MED_TILLEGSKOMPETANSE_I_HPR to false,
                HprRule.BEHANDLER_ER_KI_MED_TILLEGSKOMPETANSE_I_HPR to true,
                HprRule.SYKEFRAVAR_OVER_12_UKER to false,
            ),
        )
    }

    @Test
    fun `Behandler med flere godkjenninger, en av dem er kiropraktor med tilleggskompetanse, Status OK`() {
        val behandler =
            testBehandler(
                BehandlerScenarios
                    .AKTIV_KIROPRAKTOR_MED_TILLEGGSKOMPETANSE_OG_ANNEN_HELSEPERSONELLKATEGORI
            )
        val perioder =
            listOf(
                SykmeldingPeriode.AktivitetIkkeMulig(
                    LocalDate.of(2020, 1, 1),
                    LocalDate.of(2020, 1, 2),
                )
            )
        val signaturdato = LocalDate.of(2020, 1, 3).atStartOfDay()
        val (result) =
            HprRules(createHprRulePayload(behandler, perioder, null, signaturdato)).execute()

        assertEquals(RuleStatus.OK, result.treeResult.status)
        assertPath(
            result.rulePath,
            listOf(
                HprRule.BEHANDLER_GYLIDG_I_HPR to true,
                HprRule.BEHANDLER_HAR_AUTORISASJON_I_HPR to true,
                HprRule.BEHANDLER_ER_LEGE_I_HPR to false,
                HprRule.BEHANDLER_ER_TANNLEGE_I_HPR to false,
                HprRule.BEHANDLER_ER_MANUELLTERAPEUT_I_HPR to false,
                HprRule.BEHANDLER_ER_FT_MED_TILLEGSKOMPETANSE_I_HPR to false,
                HprRule.BEHANDLER_ER_KI_MED_TILLEGSKOMPETANSE_I_HPR to true,
                HprRule.SYKEFRAVAR_OVER_12_UKER to false,
            ),
        )
    }
}
