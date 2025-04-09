package no.nav.tsm.regulus.regula.rules.trees.hpr

import java.time.LocalDate
import java.time.LocalDateTime
import kotlin.test.Test
import kotlin.test.assertEquals
import no.nav.tsm.regulus.regula.dsl.RuleStatus
import no.nav.tsm.regulus.regula.executor.ExecutionMode
import no.nav.tsm.regulus.regula.payload.Aktivitet
import no.nav.tsm.regulus.regula.payload.SykmelderGodkjenning
import no.nav.tsm.regulus.regula.payload.SykmelderKode
import no.nav.tsm.regulus.regula.payload.SykmelderPeriode
import no.nav.tsm.regulus.regula.payload.SykmelderTilleggskompetanse
import no.nav.tsm.regulus.regula.payload.TidligereSykmelding
import no.nav.tsm.regulus.regula.rules.trees.assertPath
import no.nav.tsm.regulus.regula.testutils.december
import no.nav.tsm.regulus.regula.testutils.october
import no.nav.tsm.regulus.regula.testutils.testTidligereSykmelding

class HprRulesTest {
    private fun createHprRulePayload(
        sykmelderGodkjenninger: List<SykmelderGodkjenning>?,
        perioder: List<Aktivitet> = emptyList(),
        tidligereSykmeldinger: List<TidligereSykmelding> = emptyList(),
        signaturdato: LocalDateTime = LocalDateTime.now(),
    ) =
        HprRulePayload(
            sykmeldingId = "test-sykmelding-id",
            sykmelderGodkjenninger = sykmelderGodkjenninger,
            aktivitet = perioder,
            tidligereSykmeldinger = tidligereSykmeldinger,
            signaturdato = signaturdato,
        )

    @Test
    fun `finnes ikke i HPR, Status INVALID`() {
        val result =
            HprRules(createHprRulePayload(sykmelderGodkjenninger = null))
                .execute(ExecutionMode.NORMAL)

        assertEquals(RuleStatus.INVALID, result.treeResult.status)
        assertPath(result.rulePath, listOf(HprRule.SYKMELDER_FINNES_I_HPR to false))
        assertEquals(mapOf("harGodkjenninger" to false), result.ruleInputs)
    }

    @Test
    fun `har ikke aktiv autorisasjon, Status INVALID`() {
        val sykmelder = testSykmelderGodkjenninger(SykmelderScenarios.INAKTIV_LEGE)
        val result = HprRules(createHprRulePayload(sykmelder)).execute(ExecutionMode.NORMAL)

        assertEquals(RuleStatus.INVALID, result.treeResult.status)
        assertPath(
            result.rulePath,
            listOf(HprRule.SYKMELDER_FINNES_I_HPR to true, HprRule.SYKMELDER_GYLDIG_I_HPR to false),
        )
        assertEquals(
            mapOf("harGodkjenninger" to true, "sykmelderGodkjenninger" to sykmelder),
            result.ruleInputs,
        )
    }

    @Test
    fun `mangler autorisasjon, Status INVALID`() {
        val sykmelder = testSykmelderGodkjenninger(SykmelderScenarios.UGYLDIG_AUTORISASJON)
        val result = HprRules(createHprRulePayload(sykmelder)).execute(ExecutionMode.NORMAL)

        assertEquals(RuleStatus.INVALID, result.treeResult.status)
        assertPath(
            result.rulePath,
            listOf(
                HprRule.SYKMELDER_FINNES_I_HPR to true,
                HprRule.SYKMELDER_GYLDIG_I_HPR to true,
                HprRule.SYKMELDER_HAR_AUTORISASJON_I_HPR to false,
            ),
        )
        assertEquals(
            mapOf("harGodkjenninger" to true, "sykmelderGodkjenninger" to sykmelder),
            result.ruleInputs,
        )
    }

    @Test
    fun `LEGE har aktiv autorisasjon, Status OK`() {
        val sykmelder = testSykmelderGodkjenninger(SykmelderScenarios.AKTIV_LEGE)
        val perioder =
            listOf(Aktivitet.IkkeMulig(LocalDate.of(2020, 1, 1), LocalDate.of(2020, 1, 2)))
        val signaturdato = LocalDate.of(2020, 1, 3).atStartOfDay()
        val result =
            HprRules(
                    createHprRulePayload(
                        sykmelderGodkjenninger = sykmelder,
                        perioder = perioder,
                        signaturdato = signaturdato,
                    )
                )
                .execute(ExecutionMode.NORMAL)

        assertEquals(RuleStatus.OK, result.treeResult.status)
        assertPath(
            result.rulePath,
            listOf(
                HprRule.SYKMELDER_FINNES_I_HPR to true,
                HprRule.SYKMELDER_GYLDIG_I_HPR to true,
                HprRule.SYKMELDER_HAR_AUTORISASJON_I_HPR to true,
                HprRule.SYKMELDER_ER_LEGE_I_HPR to true,
            ),
        )
        assertEquals(
            mapOf("harGodkjenninger" to true, "sykmelderGodkjenninger" to sykmelder),
            result.ruleInputs,
        )
    }

    @Test
    fun `Manuellterapeut har aktiv autorisasjon, Status OK`() {
        val sykmelder = testSykmelderGodkjenninger(SykmelderScenarios.AKTIV_MANUELLTERAPEUT)
        val perioder =
            listOf(Aktivitet.IkkeMulig(LocalDate.of(2020, 1, 1), LocalDate.of(2020, 1, 2)))
        val signaturdato = LocalDate.of(2020, 1, 3).atStartOfDay()
        val result =
            HprRules(
                    createHprRulePayload(
                        sykmelderGodkjenninger = sykmelder,
                        perioder = perioder,
                        signaturdato = signaturdato,
                    )
                )
                .execute(ExecutionMode.NORMAL)

        assertEquals(RuleStatus.OK, result.treeResult.status)
        assertPath(
            result.rulePath,
            listOf(
                HprRule.SYKMELDER_FINNES_I_HPR to true,
                HprRule.SYKMELDER_GYLDIG_I_HPR to true,
                HprRule.SYKMELDER_HAR_AUTORISASJON_I_HPR to true,
                HprRule.SYKMELDER_ER_LEGE_I_HPR to false,
                HprRule.SYKMELDER_ER_TANNLEGE_I_HPR to false,
                HprRule.SYKMELDER_ER_MANUELLTERAPEUT_I_HPR to true,
                HprRule.SYKEFRAVAER_OVER_12_UKER to false,
            ),
        )
        assertEquals(
            mapOf(
                "harGodkjenninger" to true,
                "sykmelderGodkjenninger" to sykmelder,
                "fom" to perioder.first().fom,
                "tom" to perioder.first().tom,
                "startDatoSykefravær" to perioder.first().fom,
            ),
            result.ruleInputs,
        )
    }

    @Test
    fun `TANNLEGE har aktiv autorisasjon, Status OK`() {
        val sykmelder = testSykmelderGodkjenninger(SykmelderScenarios.AKTIV_TANNLEGE)
        val perioder =
            listOf(Aktivitet.IkkeMulig(LocalDate.of(2020, 1, 1), LocalDate.of(2020, 1, 2)))
        val signaturdato = LocalDate.of(2020, 1, 3).atStartOfDay()
        val result =
            HprRules(
                    createHprRulePayload(
                        sykmelderGodkjenninger = sykmelder,
                        perioder = perioder,
                        signaturdato = signaturdato,
                    )
                )
                .execute(ExecutionMode.NORMAL)

        assertEquals(RuleStatus.OK, result.treeResult.status)
        assertPath(
            result.rulePath,
            listOf(
                HprRule.SYKMELDER_FINNES_I_HPR to true,
                HprRule.SYKMELDER_GYLDIG_I_HPR to true,
                HprRule.SYKMELDER_HAR_AUTORISASJON_I_HPR to true,
                HprRule.SYKMELDER_ER_LEGE_I_HPR to false,
                HprRule.SYKMELDER_ER_TANNLEGE_I_HPR to true,
            ),
        )
        assertEquals(
            mapOf("harGodkjenninger" to true, "sykmelderGodkjenninger" to sykmelder),
            result.ruleInputs,
        )
    }

    @Test
    fun `Manuellterapeut har aktiv autorisasjon, sykefravær over 12 uker, Status INVALID`() {
        val sykmelder = testSykmelderGodkjenninger(SykmelderScenarios.AKTIV_MANUELLTERAPEUT)
        val perioder =
            listOf(Aktivitet.IkkeMulig(LocalDate.of(2020, 1, 1), LocalDate.of(2020, 1, 2)))
        val tidligereSykmeldinger = testTidligereSykmelding(9.october(2019), 31.december(2019))

        val startdato =
            LocalDate.of(2020, 1, 2).minusDays(85) // More than 12 weeks (84 days) before tom date
        val signaturdato = LocalDate.of(2020, 1, 3).atStartOfDay()
        val result =
            HprRules(
                    createHprRulePayload(
                        sykmelderGodkjenninger = sykmelder,
                        perioder = perioder,
                        signaturdato = signaturdato,
                        tidligereSykmeldinger = tidligereSykmeldinger,
                    )
                )
                .execute(ExecutionMode.NORMAL)

        assertEquals(RuleStatus.INVALID, result.treeResult.status)
        assertPath(
            result.rulePath,
            listOf(
                HprRule.SYKMELDER_FINNES_I_HPR to true,
                HprRule.SYKMELDER_GYLDIG_I_HPR to true,
                HprRule.SYKMELDER_HAR_AUTORISASJON_I_HPR to true,
                HprRule.SYKMELDER_ER_LEGE_I_HPR to false,
                HprRule.SYKMELDER_ER_TANNLEGE_I_HPR to false,
                HprRule.SYKMELDER_ER_MANUELLTERAPEUT_I_HPR to true,
                HprRule.SYKEFRAVAER_OVER_12_UKER to true,
            ),
        )
        assertEquals(
            mapOf(
                "harGodkjenninger" to true,
                "sykmelderGodkjenninger" to sykmelder,
                "fom" to perioder.first().fom,
                "tom" to perioder.first().tom,
                "startDatoSykefravær" to startdato,
            ),
            result.ruleInputs,
        )
    }

    @Test
    fun `Fysioterapeut uten tilleggskompetanse, Status INVALID`() {
        val sykmelder = testSykmelderGodkjenninger(SykmelderScenarios.AKTIV_FYSIOTERAPEUT)
        val perioder =
            listOf(Aktivitet.IkkeMulig(LocalDate.of(2020, 1, 1), LocalDate.of(2020, 4, 2)))
        val signaturdato = LocalDate.of(2020, 1, 3).atStartOfDay()
        val result =
            HprRules(
                    createHprRulePayload(
                        sykmelderGodkjenninger = sykmelder,
                        perioder = perioder,
                        signaturdato = signaturdato,
                    )
                )
                .execute(ExecutionMode.NORMAL)

        assertEquals(RuleStatus.INVALID, result.treeResult.status)
        assertPath(
            result.rulePath,
            listOf(
                HprRule.SYKMELDER_FINNES_I_HPR to true,
                HprRule.SYKMELDER_GYLDIG_I_HPR to true,
                HprRule.SYKMELDER_HAR_AUTORISASJON_I_HPR to true,
                HprRule.SYKMELDER_ER_LEGE_I_HPR to false,
                HprRule.SYKMELDER_ER_TANNLEGE_I_HPR to false,
                HprRule.SYKMELDER_ER_MANUELLTERAPEUT_I_HPR to false,
                HprRule.SYKMELDER_ER_FT_MED_TILLEGSKOMPETANSE_I_HPR to false,
                HprRule.SYKMELDER_ER_KI_MED_TILLEGSKOMPETANSE_I_HPR to false,
            ),
        )
        assertEquals(
            mapOf(
                "harGodkjenninger" to true,
                "sykmelderGodkjenninger" to sykmelder,
                "genereringsTidspunkt" to signaturdato,
            ),
            result.ruleInputs,
        )
    }

    @Test
    fun `Fysioterapeut med tilleggskompetanse, Status OK`() {
        val sykmelder =
            testSykmelderGodkjenninger(
                SykmelderScenarios.AKTIV_FYSIOTERAPEUT_MED_TILLEGGSKOMPETANSE
            )
        val perioder =
            listOf(Aktivitet.IkkeMulig(LocalDate.of(2020, 1, 1), LocalDate.of(2020, 1, 2)))
        val signaturdato = LocalDate.of(2020, 1, 3).atStartOfDay()
        val result =
            HprRules(
                    createHprRulePayload(
                        sykmelderGodkjenninger = sykmelder,
                        perioder = perioder,
                        signaturdato = signaturdato,
                    )
                )
                .execute(ExecutionMode.NORMAL)

        assertEquals(RuleStatus.OK, result.treeResult.status)
        assertPath(
            result.rulePath,
            listOf(
                HprRule.SYKMELDER_FINNES_I_HPR to true,
                HprRule.SYKMELDER_GYLDIG_I_HPR to true,
                HprRule.SYKMELDER_HAR_AUTORISASJON_I_HPR to true,
                HprRule.SYKMELDER_ER_LEGE_I_HPR to false,
                HprRule.SYKMELDER_ER_TANNLEGE_I_HPR to false,
                HprRule.SYKMELDER_ER_MANUELLTERAPEUT_I_HPR to false,
                HprRule.SYKMELDER_ER_FT_MED_TILLEGSKOMPETANSE_I_HPR to true,
                HprRule.SYKEFRAVAER_OVER_12_UKER to false,
            ),
        )
    }

    @Test
    fun `Fysioterapeut med tilleggskompetanse, sykefravær over 12 uker, Status INVALID`() {
        val sykmelder =
            testSykmelderGodkjenninger(
                SykmelderScenarios.AKTIV_FYSIOTERAPEUT_MED_TILLEGGSKOMPETANSE
            )
        val perioder =
            listOf(Aktivitet.IkkeMulig(LocalDate.of(2020, 1, 1), LocalDate.of(2020, 1, 2)))
        val tidligereSykmeldinger = testTidligereSykmelding(9.october(2019), 31.december(2019))
        val signaturdato = LocalDate.of(2020, 1, 3).atStartOfDay()
        val result =
            HprRules(
                    createHprRulePayload(
                        sykmelderGodkjenninger = sykmelder,
                        perioder = perioder,
                        signaturdato = signaturdato,
                        tidligereSykmeldinger = tidligereSykmeldinger,
                    )
                )
                .execute(ExecutionMode.NORMAL)

        val expectedStartDate =
            LocalDate.of(2020, 1, 2).minusDays(85) // More than 12 weeks (84 days) before tom date

        assertEquals(RuleStatus.INVALID, result.treeResult.status)
        assertPath(
            result.rulePath,
            listOf(
                HprRule.SYKMELDER_FINNES_I_HPR to true,
                HprRule.SYKMELDER_GYLDIG_I_HPR to true,
                HprRule.SYKMELDER_HAR_AUTORISASJON_I_HPR to true,
                HprRule.SYKMELDER_ER_LEGE_I_HPR to false,
                HprRule.SYKMELDER_ER_TANNLEGE_I_HPR to false,
                HprRule.SYKMELDER_ER_MANUELLTERAPEUT_I_HPR to false,
                HprRule.SYKMELDER_ER_FT_MED_TILLEGSKOMPETANSE_I_HPR to true,
                HprRule.SYKEFRAVAER_OVER_12_UKER to true,
            ),
        )
        assertEquals(
            mapOf(
                "harGodkjenninger" to true,
                "sykmelderGodkjenninger" to sykmelder,
                "genereringsTidspunkt" to signaturdato,
                "fom" to perioder.first().fom,
                "tom" to perioder.first().tom,
                "startDatoSykefravær" to expectedStartDate,
            ),
            result.ruleInputs,
        )
    }

    @Test
    fun `Kiropraktor uten tilleggskompetanse, Status INVALID`() {
        val sykmelder = testSykmelderGodkjenninger(SykmelderScenarios.AKTIV_KIROPRAKTOR)
        val perioder =
            listOf(Aktivitet.IkkeMulig(LocalDate.of(2020, 1, 1), LocalDate.of(2020, 4, 2)))
        val signaturdato = LocalDate.of(2020, 1, 3).atStartOfDay()
        val result =
            HprRules(
                    createHprRulePayload(
                        sykmelderGodkjenninger = sykmelder,
                        perioder = perioder,
                        signaturdato = signaturdato,
                    )
                )
                .execute(ExecutionMode.NORMAL)

        assertEquals(RuleStatus.INVALID, result.treeResult.status)
        assertPath(
            result.rulePath,
            listOf(
                HprRule.SYKMELDER_FINNES_I_HPR to true,
                HprRule.SYKMELDER_GYLDIG_I_HPR to true,
                HprRule.SYKMELDER_HAR_AUTORISASJON_I_HPR to true,
                HprRule.SYKMELDER_ER_LEGE_I_HPR to false,
                HprRule.SYKMELDER_ER_TANNLEGE_I_HPR to false,
                HprRule.SYKMELDER_ER_MANUELLTERAPEUT_I_HPR to false,
                HprRule.SYKMELDER_ER_FT_MED_TILLEGSKOMPETANSE_I_HPR to false,
                HprRule.SYKMELDER_ER_KI_MED_TILLEGSKOMPETANSE_I_HPR to false,
            ),
        )
        assertEquals(
            mapOf(
                "harGodkjenninger" to true,
                "sykmelderGodkjenninger" to sykmelder,
                "genereringsTidspunkt" to signaturdato,
            ),
            result.ruleInputs,
        )
    }

    @Test
    fun `Kiropraktor med tilleggskompetanse, Status OK`() {
        val sykmelder =
            testSykmelderGodkjenninger(SykmelderScenarios.AKTIV_KIROPRAKTOR_MED_TILLEGGSKOMPETANSE)
        val perioder =
            listOf(Aktivitet.IkkeMulig(LocalDate.of(2020, 1, 1), LocalDate.of(2020, 1, 2)))
        val signaturdato = LocalDate.of(2020, 1, 3).atStartOfDay()
        val result =
            HprRules(
                    createHprRulePayload(
                        sykmelderGodkjenninger = sykmelder,
                        perioder = perioder,
                        signaturdato = signaturdato,
                    )
                )
                .execute(ExecutionMode.NORMAL)

        assertEquals(RuleStatus.OK, result.treeResult.status)
        assertPath(
            result.rulePath,
            listOf(
                HprRule.SYKMELDER_FINNES_I_HPR to true,
                HprRule.SYKMELDER_GYLDIG_I_HPR to true,
                HprRule.SYKMELDER_HAR_AUTORISASJON_I_HPR to true,
                HprRule.SYKMELDER_ER_LEGE_I_HPR to false,
                HprRule.SYKMELDER_ER_TANNLEGE_I_HPR to false,
                HprRule.SYKMELDER_ER_MANUELLTERAPEUT_I_HPR to false,
                HprRule.SYKMELDER_ER_FT_MED_TILLEGSKOMPETANSE_I_HPR to false,
                HprRule.SYKMELDER_ER_KI_MED_TILLEGSKOMPETANSE_I_HPR to true,
                HprRule.SYKEFRAVAER_OVER_12_UKER to false,
            ),
        )
    }

    @Test
    fun `Kiropraktor med tilleggskompetanse, sykefravær over 12 uker, Status INVALID`() {
        val sykmelder =
            testSykmelderGodkjenninger(SykmelderScenarios.AKTIV_KIROPRAKTOR_MED_TILLEGGSKOMPETANSE)
        val perioder =
            listOf(Aktivitet.IkkeMulig(LocalDate.of(2020, 1, 1), LocalDate.of(2020, 1, 2)))
        val tidligereSykmeldinger = testTidligereSykmelding(9.october(2019), 31.december(2019))
        val startdato =
            LocalDate.of(2020, 1, 2).minusDays(85) // More than 12 weeks (84 days) before tom date
        val signaturdato = LocalDate.of(2020, 1, 3).atStartOfDay()
        val result =
            HprRules(
                    createHprRulePayload(
                        sykmelderGodkjenninger = sykmelder,
                        perioder = perioder,
                        tidligereSykmeldinger = tidligereSykmeldinger,
                        signaturdato = signaturdato,
                    )
                )
                .execute(ExecutionMode.NORMAL)

        assertEquals(RuleStatus.INVALID, result.treeResult.status)
        assertPath(
            result.rulePath,
            listOf(
                HprRule.SYKMELDER_FINNES_I_HPR to true,
                HprRule.SYKMELDER_GYLDIG_I_HPR to true,
                HprRule.SYKMELDER_HAR_AUTORISASJON_I_HPR to true,
                HprRule.SYKMELDER_ER_LEGE_I_HPR to false,
                HprRule.SYKMELDER_ER_TANNLEGE_I_HPR to false,
                HprRule.SYKMELDER_ER_MANUELLTERAPEUT_I_HPR to false,
                HprRule.SYKMELDER_ER_FT_MED_TILLEGSKOMPETANSE_I_HPR to false,
                HprRule.SYKMELDER_ER_KI_MED_TILLEGSKOMPETANSE_I_HPR to true,
                HprRule.SYKEFRAVAER_OVER_12_UKER to true,
            ),
        )
        assertEquals(
            mapOf(
                "harGodkjenninger" to true,
                "sykmelderGodkjenninger" to sykmelder,
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
        val sykmelder =
            testSykmelderGodkjenninger(
                SykmelderScenarios.AKTIV_FYSIOTERAPEUT_MED_TILLEGGSKOMPETANSE
            )

        // Override the gyldig.fra to be the same as signaturdato
        val sykmelderWithCustomGyldig =
            listOf(
                SykmelderGodkjenning(
                    autorisasjon = SykmelderKode(aktiv = true, oid = 7704, verdi = "1"),
                    helsepersonellkategori = SykmelderKode(aktiv = true, oid = 0, verdi = "FT"),
                    tillegskompetanse =
                        listOf(
                            SykmelderTilleggskompetanse(
                                avsluttetStatus = null,
                                gyldig = SykmelderPeriode(fra = signaturdato, til = null),
                                type = SykmelderKode(aktiv = true, oid = 7702, verdi = "1"),
                            )
                        ),
                )
            )

        val perioder =
            listOf(Aktivitet.IkkeMulig(LocalDate.of(2020, 1, 1), LocalDate.of(2020, 1, 2)))
        val result =
            HprRules(
                    createHprRulePayload(
                        sykmelderGodkjenninger = sykmelderWithCustomGyldig,
                        perioder = perioder,
                        signaturdato = signaturdato,
                    )
                )
                .execute(ExecutionMode.NORMAL)

        assertEquals(RuleStatus.OK, result.treeResult.status)
        assertPath(
            result.rulePath,
            listOf(
                HprRule.SYKMELDER_FINNES_I_HPR to true,
                HprRule.SYKMELDER_GYLDIG_I_HPR to true,
                HprRule.SYKMELDER_HAR_AUTORISASJON_I_HPR to true,
                HprRule.SYKMELDER_ER_LEGE_I_HPR to false,
                HprRule.SYKMELDER_ER_TANNLEGE_I_HPR to false,
                HprRule.SYKMELDER_ER_MANUELLTERAPEUT_I_HPR to false,
                HprRule.SYKMELDER_ER_FT_MED_TILLEGSKOMPETANSE_I_HPR to true,
                HprRule.SYKEFRAVAER_OVER_12_UKER to false,
            ),
        )
    }

    @Test
    fun `Fysioterapeut med tilleggskompetanse, ugyldig periode, Status INVALID`() {
        val signaturdato = LocalDate.of(2020, 1, 3).atStartOfDay()

        // Override the gyldig.fra to be one day after signaturdato
        val sykmelderWithCustomGyldig =
            listOf(
                SykmelderGodkjenning(
                    autorisasjon = SykmelderKode(aktiv = true, oid = 7704, verdi = "1"),
                    helsepersonellkategori = SykmelderKode(aktiv = true, oid = 0, verdi = "FT"),
                    tillegskompetanse =
                        listOf(
                            SykmelderTilleggskompetanse(
                                avsluttetStatus = null,
                                gyldig =
                                    SykmelderPeriode(fra = signaturdato.plusDays(1), til = null),
                                type = SykmelderKode(aktiv = true, oid = 7702, verdi = "1"),
                            )
                        ),
                )
            )

        val perioder =
            listOf(Aktivitet.IkkeMulig(LocalDate.of(2020, 1, 1), LocalDate.of(2020, 1, 2)))
        val result =
            HprRules(
                    createHprRulePayload(
                        sykmelderGodkjenninger = sykmelderWithCustomGyldig,
                        perioder = perioder,
                        signaturdato = signaturdato,
                    )
                )
                .execute(ExecutionMode.NORMAL)

        assertEquals(RuleStatus.INVALID, result.treeResult.status)
        assertPath(
            result.rulePath,
            listOf(
                HprRule.SYKMELDER_FINNES_I_HPR to true,
                HprRule.SYKMELDER_GYLDIG_I_HPR to true,
                HprRule.SYKMELDER_HAR_AUTORISASJON_I_HPR to true,
                HprRule.SYKMELDER_ER_LEGE_I_HPR to false,
                HprRule.SYKMELDER_ER_TANNLEGE_I_HPR to false,
                HprRule.SYKMELDER_ER_MANUELLTERAPEUT_I_HPR to false,
                HprRule.SYKMELDER_ER_FT_MED_TILLEGSKOMPETANSE_I_HPR to false,
                HprRule.SYKMELDER_ER_KI_MED_TILLEGSKOMPETANSE_I_HPR to false,
            ),
        )
        assertEquals(
            mapOf(
                "harGodkjenninger" to true,
                "sykmelderGodkjenninger" to sykmelderWithCustomGyldig,
                "genereringsTidspunkt" to signaturdato,
            ),
            result.ruleInputs,
        )
    }

    @Test
    fun `Fysioterapeut med tilleggskompetanse, ugyldig periode tom før genereringstidspunkt, Status INVALID`() {
        val signaturdato = LocalDate.of(2020, 1, 3).atStartOfDay()

        // Override the gyldig.til to be one day before signaturdato
        val sykmelderWithCustomGyldig =
            listOf(
                SykmelderGodkjenning(
                    autorisasjon = SykmelderKode(aktiv = true, oid = 7704, verdi = "1"),
                    helsepersonellkategori = SykmelderKode(aktiv = true, oid = 0, verdi = "FT"),
                    tillegskompetanse =
                        listOf(
                            SykmelderTilleggskompetanse(
                                avsluttetStatus = null,
                                gyldig =
                                    SykmelderPeriode(
                                        fra = signaturdato.minusDays(10),
                                        til = signaturdato.minusDays(1),
                                    ),
                                type = SykmelderKode(aktiv = true, oid = 7702, verdi = "1"),
                            )
                        ),
                )
            )

        val perioder =
            listOf(Aktivitet.IkkeMulig(LocalDate.of(2020, 1, 1), LocalDate.of(2020, 1, 2)))
        val result =
            HprRules(
                    createHprRulePayload(
                        sykmelderWithCustomGyldig,
                        perioder = perioder,
                        signaturdato = signaturdato,
                    )
                )
                .execute(ExecutionMode.NORMAL)

        assertEquals(RuleStatus.INVALID, result.treeResult.status)
        assertPath(
            result.rulePath,
            listOf(
                HprRule.SYKMELDER_FINNES_I_HPR to true,
                HprRule.SYKMELDER_GYLDIG_I_HPR to true,
                HprRule.SYKMELDER_HAR_AUTORISASJON_I_HPR to true,
                HprRule.SYKMELDER_ER_LEGE_I_HPR to false,
                HprRule.SYKMELDER_ER_TANNLEGE_I_HPR to false,
                HprRule.SYKMELDER_ER_MANUELLTERAPEUT_I_HPR to false,
                HprRule.SYKMELDER_ER_FT_MED_TILLEGSKOMPETANSE_I_HPR to false,
                HprRule.SYKMELDER_ER_KI_MED_TILLEGSKOMPETANSE_I_HPR to false,
            ),
        )
        assertEquals(
            mapOf(
                "harGodkjenninger" to true,
                "sykmelderGodkjenninger" to sykmelderWithCustomGyldig,
                "genereringsTidspunkt" to signaturdato,
            ),
            result.ruleInputs,
        )
    }

    @Test
    fun `Fysioterapeut med tilleggskompetanse, gyldig periode tom samme som genereringstidspunkt, Status OK`() {
        val signaturdato = LocalDate.of(2020, 1, 3).atStartOfDay()

        // Override the gyldig.til to be the same as signaturdato
        val sykmelderWithCustomGyldig =
            listOf(
                SykmelderGodkjenning(
                    autorisasjon = SykmelderKode(aktiv = true, oid = 7704, verdi = "1"),
                    helsepersonellkategori = SykmelderKode(aktiv = true, oid = 0, verdi = "FT"),
                    tillegskompetanse =
                        listOf(
                            SykmelderTilleggskompetanse(
                                avsluttetStatus = null,
                                gyldig =
                                    SykmelderPeriode(
                                        fra = signaturdato.minusDays(10),
                                        til = signaturdato,
                                    ),
                                type = SykmelderKode(aktiv = true, oid = 7702, verdi = "1"),
                            )
                        ),
                )
            )

        val perioder =
            listOf(Aktivitet.IkkeMulig(LocalDate.of(2020, 1, 1), LocalDate.of(2020, 1, 2)))
        val result =
            HprRules(
                    createHprRulePayload(
                        sykmelderWithCustomGyldig,
                        perioder,
                        signaturdato = signaturdato,
                    )
                )
                .execute(ExecutionMode.NORMAL)

        assertEquals(RuleStatus.OK, result.treeResult.status)
        assertPath(
            result.rulePath,
            listOf(
                HprRule.SYKMELDER_FINNES_I_HPR to true,
                HprRule.SYKMELDER_GYLDIG_I_HPR to true,
                HprRule.SYKMELDER_HAR_AUTORISASJON_I_HPR to true,
                HprRule.SYKMELDER_ER_LEGE_I_HPR to false,
                HprRule.SYKMELDER_ER_TANNLEGE_I_HPR to false,
                HprRule.SYKMELDER_ER_MANUELLTERAPEUT_I_HPR to false,
                HprRule.SYKMELDER_ER_FT_MED_TILLEGSKOMPETANSE_I_HPR to true,
                HprRule.SYKEFRAVAER_OVER_12_UKER to false,
            ),
        )
    }

    @Test
    fun `Fysioterapeut med feil tilleggskompetanse type verdi, Status INVALID`() {
        val sykmelder =
            testSykmelderGodkjenninger(
                SykmelderScenarios.AKTIV_FYSIOTERAPEUT_MED_FEIL_TILLEGGSKOMPETANSE_TYPE
            )
        val perioder =
            listOf(Aktivitet.IkkeMulig(LocalDate.of(2020, 1, 1), LocalDate.of(2020, 1, 2)))
        val signaturdato = LocalDate.of(2020, 1, 3).atStartOfDay()
        val result =
            HprRules(
                    createHprRulePayload(
                        sykmelderGodkjenninger = sykmelder,
                        perioder = perioder,
                        signaturdato = signaturdato,
                    )
                )
                .execute(ExecutionMode.NORMAL)

        assertEquals(RuleStatus.INVALID, result.treeResult.status)
        assertPath(
            result.rulePath,
            listOf(
                HprRule.SYKMELDER_FINNES_I_HPR to true,
                HprRule.SYKMELDER_GYLDIG_I_HPR to true,
                HprRule.SYKMELDER_HAR_AUTORISASJON_I_HPR to true,
                HprRule.SYKMELDER_ER_LEGE_I_HPR to false,
                HprRule.SYKMELDER_ER_TANNLEGE_I_HPR to false,
                HprRule.SYKMELDER_ER_MANUELLTERAPEUT_I_HPR to false,
                HprRule.SYKMELDER_ER_FT_MED_TILLEGSKOMPETANSE_I_HPR to false,
                HprRule.SYKMELDER_ER_KI_MED_TILLEGSKOMPETANSE_I_HPR to false,
            ),
        )
        assertEquals(
            mapOf(
                "harGodkjenninger" to true,
                "sykmelderGodkjenninger" to sykmelder,
                "genereringsTidspunkt" to signaturdato,
            ),
            result.ruleInputs,
        )
    }

    @Test
    fun `Fysioterapeut med inaktiv tilleggskompetanse, Status INVALID`() {
        val sykmelder =
            testSykmelderGodkjenninger(
                SykmelderScenarios.AKTIV_FYSIOTERAPEUT_MED_INAKTIV_TILLEGGSKOMPETANSE
            )
        val perioder =
            listOf(Aktivitet.IkkeMulig(LocalDate.of(2020, 1, 1), LocalDate.of(2020, 1, 2)))
        val signaturdato = LocalDate.of(2020, 1, 3).atStartOfDay()
        val result =
            HprRules(
                    createHprRulePayload(
                        sykmelderGodkjenninger = sykmelder,
                        perioder = perioder,
                        signaturdato = signaturdato,
                    )
                )
                .execute(ExecutionMode.NORMAL)

        assertEquals(RuleStatus.INVALID, result.treeResult.status)
        assertPath(
            result.rulePath,
            listOf(
                HprRule.SYKMELDER_FINNES_I_HPR to true,
                HprRule.SYKMELDER_GYLDIG_I_HPR to true,
                HprRule.SYKMELDER_HAR_AUTORISASJON_I_HPR to true,
                HprRule.SYKMELDER_ER_LEGE_I_HPR to false,
                HprRule.SYKMELDER_ER_TANNLEGE_I_HPR to false,
                HprRule.SYKMELDER_ER_MANUELLTERAPEUT_I_HPR to false,
                HprRule.SYKMELDER_ER_FT_MED_TILLEGSKOMPETANSE_I_HPR to false,
                HprRule.SYKMELDER_ER_KI_MED_TILLEGSKOMPETANSE_I_HPR to false,
            ),
        )
        assertEquals(
            mapOf(
                "harGodkjenninger" to true,
                "sykmelderGodkjenninger" to sykmelder,
                "genereringsTidspunkt" to signaturdato,
            ),
            result.ruleInputs,
        )
    }

    @Test
    fun `Kiropraktor med tilleggskompetanse og annen helsepersonellkategori, Status OK`() {
        val sykmelder =
            testSykmelderGodkjenninger(SykmelderScenarios.AKTIV_KIROPRAKTOR_MED_TILLEGGSKOMPETANSE)
        val perioder =
            listOf(Aktivitet.IkkeMulig(LocalDate.of(2020, 1, 1), LocalDate.of(2020, 1, 2)))
        val signaturdato = LocalDate.of(2020, 1, 3).atStartOfDay()
        val result =
            HprRules(
                    createHprRulePayload(
                        sykmelderGodkjenninger = sykmelder,
                        perioder = perioder,
                        signaturdato = signaturdato,
                    )
                )
                .execute(ExecutionMode.NORMAL)

        assertEquals(RuleStatus.OK, result.treeResult.status)
        assertPath(
            result.rulePath,
            listOf(
                HprRule.SYKMELDER_FINNES_I_HPR to true,
                HprRule.SYKMELDER_GYLDIG_I_HPR to true,
                HprRule.SYKMELDER_HAR_AUTORISASJON_I_HPR to true,
                HprRule.SYKMELDER_ER_LEGE_I_HPR to false,
                HprRule.SYKMELDER_ER_TANNLEGE_I_HPR to false,
                HprRule.SYKMELDER_ER_MANUELLTERAPEUT_I_HPR to false,
                HprRule.SYKMELDER_ER_FT_MED_TILLEGSKOMPETANSE_I_HPR to false,
                HprRule.SYKMELDER_ER_KI_MED_TILLEGSKOMPETANSE_I_HPR to true,
                HprRule.SYKEFRAVAER_OVER_12_UKER to false,
            ),
        )
    }

    @Test
    fun `Sykmelder med flere godkjenninger, en av dem er kiropraktor med tilleggskompetanse, Status OK`() {
        val sykmelder =
            testSykmelderGodkjenninger(
                SykmelderScenarios
                    .AKTIV_KIROPRAKTOR_MED_TILLEGGSKOMPETANSE_OG_ANNEN_HELSEPERSONELLKATEGORI
            )
        val perioder =
            listOf(Aktivitet.IkkeMulig(LocalDate.of(2020, 1, 1), LocalDate.of(2020, 1, 2)))
        val signaturdato = LocalDate.of(2020, 1, 3).atStartOfDay()
        val result =
            HprRules(
                    createHprRulePayload(
                        sykmelderGodkjenninger = sykmelder,
                        perioder = perioder,
                        signaturdato = signaturdato,
                    )
                )
                .execute(ExecutionMode.NORMAL)

        assertEquals(RuleStatus.OK, result.treeResult.status)
        assertPath(
            result.rulePath,
            listOf(
                HprRule.SYKMELDER_FINNES_I_HPR to true,
                HprRule.SYKMELDER_GYLDIG_I_HPR to true,
                HprRule.SYKMELDER_HAR_AUTORISASJON_I_HPR to true,
                HprRule.SYKMELDER_ER_LEGE_I_HPR to false,
                HprRule.SYKMELDER_ER_TANNLEGE_I_HPR to false,
                HprRule.SYKMELDER_ER_MANUELLTERAPEUT_I_HPR to false,
                HprRule.SYKMELDER_ER_FT_MED_TILLEGSKOMPETANSE_I_HPR to false,
                HprRule.SYKMELDER_ER_KI_MED_TILLEGSKOMPETANSE_I_HPR to true,
                HprRule.SYKEFRAVAER_OVER_12_UKER to false,
            ),
        )
    }
}
