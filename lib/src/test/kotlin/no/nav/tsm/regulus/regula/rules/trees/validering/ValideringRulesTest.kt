package no.nav.tsm.regulus.regula.rules.trees.validering

import java.time.LocalDate
import kotlin.test.*
import kotlin.test.Test
import no.nav.tsm.regulus.regula.dsl.RuleStatus
import no.nav.tsm.regulus.regula.dsl.getOutcome
import no.nav.tsm.regulus.regula.executor.ExecutionMode
import no.nav.tsm.regulus.regula.payload.Aktivitet
import no.nav.tsm.regulus.regula.rules.trees.assertPath
import no.nav.tsm.regulus.regula.testutils.generatePersonNumber

class ValideringRulesTest {
    @Test
    fun `Alt ok, Status OK`() {
        val person14Years = LocalDate.now().minusYears(14)
        val pasientFnr = generatePersonNumber(person14Years)

        val result =
            ValideringRules(
                    ValideringRulePayload(
                        sykmeldingId = "sykmeldingId",
                        rulesetVersion = "2",
                        aktivitet = enAktivitet,
                        legekontorOrgnr = "123123123",
                        behandlerFnr = "08201023912",
                        avsenderFnr = "01912391932",
                        pasientIdent = pasientFnr,
                        utdypendeOpplysninger = emptyMap(),
                        papirsykmelding = false,
                        tidligereSykmeldinger = emptyList(),
                    )
                )
                .execute(ExecutionMode.NORMAL)

        assertEquals(result.treeResult.status, RuleStatus.OK)
        assertPath(
            result.rulePath,
            listOf(
                ValideringRule.UGYLDIG_ORGNR_LENGDE to false,
                ValideringRule.PAPIRSYKMELDING to false,
                ValideringRule.UGYLDIG_REGELSETTVERSJON to false,
                ValideringRule.MANGLENDE_DYNAMISKE_SPOERSMAL_VERSJON2_UKE_39 to false,
                ValideringRule.AVSENDER_FNR_ER_SAMME_SOM_PASIENT_FNR to false,
                ValideringRule.BEHANDLER_FNR_ER_SAMME_SOM_PASIENT_FNR to false,
            ),
        )

        assertEquals(
            result.ruleInputs,
            mapOf(
                "rulesetVersion" to "2",
                "papirsykmelding" to false,
                "sykmeldingPerioder" to enAktivitet,
                "utdypendeOpplysninger" to emptyMap<String, Any>(),
                "pasientIdent" to pasientFnr,
                "legekontorOrgnr" to "123123123",
                "avsenderFnr" to "01912391932",
                "behandlerFnr" to "08201023912",
            ),
        )

        assertNull(result.treeResult.getOutcome())
    }

    @Test
    fun `Ugyldig regelsettversjon, Status INVALID`() {
        val result =
            ValideringRules(
                    ValideringRulePayload(
                        sykmeldingId = "sykmeldingId",
                        rulesetVersion = "69",
                        aktivitet = enAktivitet,
                        legekontorOrgnr = "123123123",
                        behandlerFnr = "08201023912",
                        avsenderFnr = "01912391932",
                        pasientIdent = "07091912345",
                        utdypendeOpplysninger = emptyMap(),
                        papirsykmelding = false,
                        tidligereSykmeldinger = emptyList(),
                    )
                )
                .execute(ExecutionMode.NORMAL)

        assertEquals(result.treeResult.status, RuleStatus.INVALID)
        assertPath(
            result.rulePath,
            listOf(
                ValideringRule.UGYLDIG_ORGNR_LENGDE to false,
                ValideringRule.PAPIRSYKMELDING to false,
                ValideringRule.UGYLDIG_REGELSETTVERSJON to true,
            ),
        )

        assertEquals(
            result.ruleInputs,
            mapOf(
                "rulesetVersion" to "69",
                "papirsykmelding" to false,
                "legekontorOrgnr" to "123123123",
            ),
        )

        assertEquals(
            result.treeResult.getOutcome(),
            ValideringRule.Outcomes.UGYLDIG_REGELSETTVERSJON,
        )
    }

    @Test
    fun `Mangelde dynamiske sporsmaal versjon 2 uke39, Status INVALID`() {
        val perioderMedFomForDritlengesiden =
            listOf(Aktivitet.IkkeMulig(fom = LocalDate.now().minusDays(274), tom = LocalDate.now()))
        val result =
            ValideringRules(
                    ValideringRulePayload(
                        sykmeldingId = "sykmeldingId",
                        rulesetVersion = "2",
                        aktivitet = perioderMedFomForDritlengesiden,
                        legekontorOrgnr = "123123123",
                        behandlerFnr = "08201023912",
                        avsenderFnr = "01912391932",
                        pasientIdent = "07091912345",
                        utdypendeOpplysninger = emptyMap(),
                        papirsykmelding = false,
                        tidligereSykmeldinger = emptyList(),
                    )
                )
                .execute(ExecutionMode.NORMAL)

        assertEquals(result.treeResult.status, RuleStatus.INVALID)
        assertPath(
            result.rulePath,
            listOf(
                ValideringRule.UGYLDIG_ORGNR_LENGDE to false,
                ValideringRule.PAPIRSYKMELDING to false,
                ValideringRule.UGYLDIG_REGELSETTVERSJON to false,
                ValideringRule.MANGLENDE_DYNAMISKE_SPOERSMAL_VERSJON2_UKE_39 to true,
            ),
        )
        assertEquals(
            result.ruleInputs,
            mapOf(
                "rulesetVersion" to "2",
                "legekontorOrgnr" to "123123123",
                "papirsykmelding" to false,
                "sykmeldingPerioder" to perioderMedFomForDritlengesiden,
                "utdypendeOpplysninger" to emptyMap<String, Any>(),
            ),
        )

        assertEquals(
            result.treeResult.getOutcome(),
            ValideringRule.Outcomes.MANGLENDE_DYNAMISKE_SPOERSMAL_VERSJON2_UKE_39,
        )
    }

    @Test
    fun `Manglende en av de dynamiske sporsmalene for ruleset v2 uke 39, INVALID`() {
        val perioderMedFomForDritlengesiden =
            listOf(Aktivitet.IkkeMulig(fom = LocalDate.now().minusDays(274), tom = LocalDate.now()))
        val utdypendeOpplysninger =
            mapOf(
                "6.5" to
                    mapOf(
                        "6.5.1" to
                            mapOf(
                                "svar" to
                                    "Thomson designs troy ratings differently york arrived, leaders scared done stanford assess package vegetarian, birmingham son preparation take forgot over hearings, being. "
                            ),
                        "6.5.3" to
                            mapOf(
                                "svar" to
                                    "Outline communist rear charming therapist grab pendant, reliability flesh acquired champagne typing keep strip, conjunction compact stuffed witness nova invisible setup. "
                            ),
                        "6.5.4" to
                            mapOf(
                                "svar" to
                                    "Font trains vaccine assistant nano feedback inexpensive, web charles malpractice field dodge statewide redhead, velocity movies shot converter biz journey filename, occasion aerospace holland tank. "
                            ),
                    )
            )

        val result =
            ValideringRules(
                    ValideringRulePayload(
                        sykmeldingId = "sykmeldingId",
                        rulesetVersion = "2",
                        aktivitet = perioderMedFomForDritlengesiden,
                        legekontorOrgnr = null,
                        behandlerFnr = "07091912345",
                        pasientIdent = "08201023912",
                        avsenderFnr = "08201023913",
                        utdypendeOpplysninger = utdypendeOpplysninger,
                        papirsykmelding = false,
                        tidligereSykmeldinger = emptyList(),
                    )
                )
                .execute(ExecutionMode.NORMAL)

        assertEquals(result.treeResult.status, RuleStatus.INVALID)
        assertPath(
            result.rulePath,
            listOf(
                ValideringRule.UGYLDIG_ORGNR_LENGDE to false,
                ValideringRule.PAPIRSYKMELDING to false,
                ValideringRule.UGYLDIG_REGELSETTVERSJON to false,
                ValideringRule.MANGLENDE_DYNAMISKE_SPOERSMAL_VERSJON2_UKE_39 to true,
            ),
        )

        assertEquals(
            result.ruleInputs - "utdypendeOpplysninger",
            mapOf(
                "rulesetVersion" to "2",
                "papirsykmelding" to false,
                "sykmeldingPerioder" to perioderMedFomForDritlengesiden,
                "legekontorOrgnr" to "",
            ),
        )

        assertEquals(
            result.treeResult.getOutcome(),
            ValideringRule.Outcomes.MANGLENDE_DYNAMISKE_SPOERSMAL_VERSJON2_UKE_39,
        )
    }

    @Test
    fun `Behandler samme som pasient, Status INVALID`() {
        val pasientFnr = generatePersonNumber(LocalDate.now().minusYears(31), false)

        val result =
            ValideringRules(
                    ValideringRulePayload(
                        sykmeldingId = "sykmeldingId",
                        rulesetVersion = "2",
                        aktivitet = enAktivitet,
                        legekontorOrgnr = null,
                        behandlerFnr = pasientFnr,
                        pasientIdent = pasientFnr,
                        avsenderFnr = "08201023912",
                        utdypendeOpplysninger = emptyMap(),
                        papirsykmelding = false,
                        tidligereSykmeldinger = emptyList(),
                    )
                )
                .execute(ExecutionMode.NORMAL)

        assertEquals(result.treeResult.status, RuleStatus.INVALID)
        assertPath(
            result.rulePath,
            listOf(
                ValideringRule.UGYLDIG_ORGNR_LENGDE to false,
                ValideringRule.PAPIRSYKMELDING to false,
                ValideringRule.UGYLDIG_REGELSETTVERSJON to false,
                ValideringRule.MANGLENDE_DYNAMISKE_SPOERSMAL_VERSJON2_UKE_39 to false,
                ValideringRule.AVSENDER_FNR_ER_SAMME_SOM_PASIENT_FNR to false,
                ValideringRule.BEHANDLER_FNR_ER_SAMME_SOM_PASIENT_FNR to true,
            ),
        )

        assertEquals(
            result.ruleInputs,
            mapOf(
                "rulesetVersion" to "2",
                "papirsykmelding" to false,
                "sykmeldingPerioder" to enAktivitet,
                "utdypendeOpplysninger" to emptyMap<String, Any>(),
                "legekontorOrgnr" to "",
                "avsenderFnr" to "08201023912",
                "pasientIdent" to pasientFnr,
                "behandlerFnr" to pasientFnr,
            ),
        )

        assertEquals(
            result.treeResult.getOutcome(),
            ValideringRule.Outcomes.BEHANDLER_FNR_ER_SAMME_SOM_PASIENT_FNR,
        )
    }

    @Test
    fun `Ugyldig orgnummer lengde, Status INVALID`() {
        val person31Years = LocalDate.now().minusYears(31)
        val pasientFnr = generatePersonNumber(person31Years, false)

        val result =
            ValideringRules(
                    ValideringRulePayload(
                        sykmeldingId = "sykmeldingId",
                        rulesetVersion = "2",
                        aktivitet = enAktivitet,
                        legekontorOrgnr = "1232344",
                        behandlerFnr = "08201023912",
                        avsenderFnr = "2",
                        pasientIdent = pasientFnr,
                        utdypendeOpplysninger = emptyMap(),
                        papirsykmelding = false,
                        tidligereSykmeldinger = emptyList(),
                    )
                )
                .execute(ExecutionMode.NORMAL)

        assertEquals(result.treeResult.status, RuleStatus.INVALID)
        assertPath(result.rulePath, listOf(ValideringRule.UGYLDIG_ORGNR_LENGDE to true))

        assertEquals(result.ruleInputs, mapOf("legekontorOrgnr" to "1232344"))

        assertEquals(result.treeResult.getOutcome(), ValideringRule.Outcomes.UGYLDIG_ORGNR_LENGDE)
    }

    @Test
    fun `Avsender samme som pasient, Status INVALID`() {
        val pasientFnr = generatePersonNumber(LocalDate.now().minusYears(31), false)

        val result =
            ValideringRules(
                    ValideringRulePayload(
                        sykmeldingId = "sykmeldingId",
                        rulesetVersion = "3",
                        aktivitet = enAktivitet,
                        legekontorOrgnr = null,
                        behandlerFnr = "08201023912",
                        avsenderFnr = pasientFnr,
                        pasientIdent = pasientFnr,
                        utdypendeOpplysninger = emptyMap(),
                        papirsykmelding = false,
                        tidligereSykmeldinger = emptyList(),
                    )
                )
                .execute(ExecutionMode.NORMAL)

        assertEquals(result.treeResult.status, RuleStatus.INVALID)
        assertPath(
            result.rulePath,
            listOf(
                ValideringRule.UGYLDIG_ORGNR_LENGDE to false,
                ValideringRule.PAPIRSYKMELDING to false,
                ValideringRule.UGYLDIG_REGELSETTVERSJON to false,
                ValideringRule.MANGLENDE_DYNAMISKE_SPOERSMAL_VERSJON2_UKE_39 to false,
                ValideringRule.AVSENDER_FNR_ER_SAMME_SOM_PASIENT_FNR to true,
            ),
        )

        assertEquals(
            result.ruleInputs,
            mapOf(
                "rulesetVersion" to "3",
                "papirsykmelding" to false,
                "sykmeldingPerioder" to enAktivitet,
                "utdypendeOpplysninger" to emptyMap<String, Any>(),
                "legekontorOrgnr" to "",
                "avsenderFnr" to pasientFnr,
                "pasientIdent" to pasientFnr,
            ),
        )

        assertEquals(
            result.treeResult.getOutcome(),
            ValideringRule.Outcomes.AVSENDER_FNR_ER_SAMME_SOM_PASIENT_FNR,
        )
    }

    private val enAktivitet: List<Aktivitet> =
        listOf(
            Aktivitet.IkkeMulig(
                fom = LocalDate.now().minusDays(1),
                tom = LocalDate.now().plusDays(1),
            )
        )
}
