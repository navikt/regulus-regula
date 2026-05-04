package no.nav.tsm.regulus.regula.rules.trees.validering

import java.time.LocalDate
import kotlin.collections.emptyList
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
                        rulesetVersion = "2",
                        aktivitet = emptyList(),
                        legekontorOrgnr = "123123123",
                        behandlerFnr = "08201023912",
                        avsenderFnr = "01912391932",
                        pasientIdent = pasientFnr,
                        besvarteUtypendeOpplysninger = emptyList(),
                        papirsykmelding = false,
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
                "sykmeldingPerioder" to emptyList<Any>(),
                "utdypendeOpplysninger" to emptyList<String>(),
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
                        rulesetVersion = "69",
                        aktivitet = emptyList(),
                        legekontorOrgnr = "123123123",
                        behandlerFnr = "08201023912",
                        avsenderFnr = "01912391932",
                        pasientIdent = "07091912345",
                        besvarteUtypendeOpplysninger = emptyList(),
                        papirsykmelding = false,
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
                        rulesetVersion = "2",
                        aktivitet = perioderMedFomForDritlengesiden,
                        legekontorOrgnr = "123123123",
                        behandlerFnr = "08201023912",
                        avsenderFnr = "01912391932",
                        pasientIdent = "07091912345",
                        besvarteUtypendeOpplysninger = emptyList(),
                        papirsykmelding = false,
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
                "utdypendeOpplysninger" to emptyList<String>(),
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
        val utdypendeOpplysninger = listOf("6.5.1", "6.5.3", "6.5.4")

        val result =
            ValideringRules(
                    ValideringRulePayload(
                        rulesetVersion = "2",
                        aktivitet = perioderMedFomForDritlengesiden,
                        legekontorOrgnr = null,
                        behandlerFnr = "07091912345",
                        pasientIdent = "08201023912",
                        avsenderFnr = "08201023913",
                        besvarteUtypendeOpplysninger = utdypendeOpplysninger,
                        papirsykmelding = false,
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
                        rulesetVersion = "2",
                        aktivitet = emptyList(),
                        legekontorOrgnr = null,
                        behandlerFnr = pasientFnr,
                        pasientIdent = pasientFnr,
                        avsenderFnr = "08201023912",
                        besvarteUtypendeOpplysninger = emptyList(),
                        papirsykmelding = false,
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
                "sykmeldingPerioder" to emptyList<Any>(),
                "utdypendeOpplysninger" to emptyList<String>(),
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
    fun `Behandler finnes ikke shouldn't trigger any rules`() {
        val pasientFnr = generatePersonNumber(LocalDate.now().minusYears(31), false)

        val result =
            ValideringRules(
                    ValideringRulePayload(
                        rulesetVersion = "2",
                        aktivitet = emptyList(),
                        legekontorOrgnr = null,
                        behandlerFnr = null,
                        pasientIdent = pasientFnr,
                        avsenderFnr = "08201023912",
                        besvarteUtypendeOpplysninger = emptyList(),
                        papirsykmelding = false,
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
                "sykmeldingPerioder" to emptyList<Any>(),
                "utdypendeOpplysninger" to emptyList<String>(),
                "legekontorOrgnr" to "",
                "avsenderFnr" to "08201023912",
                "pasientIdent" to pasientFnr,
                "behandlerFnr" to "mangler",
            ),
        )

        assertNull(result.treeResult.getOutcome())
    }

    @Test
    fun `Ugyldig orgnummer lengde, Status INVALID`() {
        val person31Years = LocalDate.now().minusYears(31)
        val pasientFnr = generatePersonNumber(person31Years, false)

        val result =
            ValideringRules(
                    ValideringRulePayload(
                        rulesetVersion = "2",
                        aktivitet = emptyList(),
                        legekontorOrgnr = "1232344",
                        behandlerFnr = "08201023912",
                        avsenderFnr = "2",
                        pasientIdent = pasientFnr,
                        besvarteUtypendeOpplysninger = emptyList(),
                        papirsykmelding = false,
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
                        rulesetVersion = "3",
                        aktivitet = emptyList(),
                        legekontorOrgnr = null,
                        behandlerFnr = "08201023912",
                        avsenderFnr = pasientFnr,
                        pasientIdent = pasientFnr,
                        besvarteUtypendeOpplysninger = emptyList(),
                        papirsykmelding = false,
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
                "sykmeldingPerioder" to emptyList<Any>(),
                "utdypendeOpplysninger" to emptyList<String>(),
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
}
