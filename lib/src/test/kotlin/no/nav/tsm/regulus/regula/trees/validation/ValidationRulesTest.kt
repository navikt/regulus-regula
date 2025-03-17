package no.nav.tsm.regulus.regula.trees.validation

import java.time.LocalDate
import kotlin.test.*
import kotlin.test.Test
import no.nav.tsm.regulus.regula.executor.RuleStatus
import no.nav.tsm.regulus.regula.trees.generatePersonNumber

class ValidationRulesTest {
    @Test
    fun `Alt ok, Status OK`() {
        val person14Years = LocalDate.now().minusYears(14)
        val pasientFnr = generatePersonNumber(person14Years)

        val (result) =
            ValidationRules(
                    ValidationRulePayload(
                        sykmeldingId = "sykmeldingId",
                        rulesetVersion = "2",
                        perioder = emptyList(),
                        legekontorOrgnr = "123123123",
                        behandlerFnr = "08201023912",
                        avsenderFnr = "01912391932",
                        patientPersonNumber = pasientFnr,
                        utdypendeOpplysninger = emptyMap(),
                    )
                )
                .execute()

        assertEquals(result.treeResult.status, RuleStatus.OK)
        assertEquals(
            result.rulePath.map { it.rule to it.ruleResult },
            listOf(
                ValidationRule.UGYLDIG_REGELSETTVERSJON to false,
                ValidationRule.MANGLENDE_DYNAMISKE_SPOERSMAL_VERSJON2_UKE_39 to false,
                ValidationRule.UGYLDIG_ORGNR_LENGDE to false,
                ValidationRule.AVSENDER_FNR_ER_SAMME_SOM_PASIENT_FNR to false,
                ValidationRule.BEHANDLER_FNR_ER_SAMME_SOM_PASIENT_FNR to false,
            ),
        )

        assertEquals(
            result.ruleInputs,
            mapOf(
                "rulesetVersion" to "2",
                "sykmeldingPerioder" to emptyList<Any>(),
                "utdypendeOpplysninger" to emptyMap<String, Any>(),
                "pasientIdent" to pasientFnr,
                "legekontorOrgnr" to "123123123",
                "avsenderFnr" to "01912391932",
                "behandlerFnr" to "08201023912",
            ),
        )

        assertNull(result.treeResult.ruleOutcome)
    }

    @Test fun `Ugyldig regelsettversjon, Status INVALID`() {}

    @Test fun `Mangelde dynamiske sporsmaal versjon 2 uke39, Status INVALID`() {}

    @Test fun `Ugyldig orgnummer lengede, Status INVALID`() {}

    @Test fun `Avsender samme som pasient, Status INVALID`() {}

    @Test fun `Behandler samme som pasient, Status INVALID`() {}
}
