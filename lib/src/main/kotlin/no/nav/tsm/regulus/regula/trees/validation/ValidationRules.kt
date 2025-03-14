package no.nav.tsm.regulus.regula.trees.validation

import no.nav.tsm.regulus.regula.dsl.RuleOutput
import no.nav.tsm.regulus.regula.executor.TreeExecutor
import no.nav.tsm.regulus.regula.utils.daysBetween

class ValidationRulesExecutor(validationRulePayload: ValidationRulePayload) :
    TreeExecutor<ValidationRule, ValidationRulePayload>(validationRuleTree, validationRulePayload) {
    override fun getRule(rule: ValidationRule) = getValidationRule(rule)
}

private typealias ValidationRuleFn = (payload: ValidationRulePayload) -> RuleOutput<ValidationRule>

fun getValidationRule(rules: ValidationRule): ValidationRuleFn =
    when (rules) {
        ValidationRule.UGYLDIG_REGELSETTVERSJON -> Rules.ugyldigRegelsettversjon
        ValidationRule.MANGLENDE_DYNAMISKE_SPOERSMAL_VERSJON2_UKE_39 ->
            Rules.manglendeDynamiskesporsmaalversjon2uke39
        ValidationRule.UGYLDIG_ORGNR_LENGDE -> Rules.ugyldingOrgNummerLengde
        ValidationRule.AVSENDER_FNR_ER_SAMME_SOM_PASIENT_FNR -> Rules.avsenderSammeSomPasient
        ValidationRule.BEHANDLER_FNR_ER_SAMME_SOM_PASIENT_FNR -> Rules.behandlerSammeSomPasient
    }

private val Rules =
    object {
        val ugyldigRegelsettversjon: ValidationRuleFn = { payload ->
            val rulesetVersion = payload.rulesetVersion

            RuleOutput(
                ruleInputs = mapOf("rulesetVersion" to rulesetVersion),
                rule = ValidationRule.UGYLDIG_REGELSETTVERSJON,
                ruleResult = rulesetVersion !in arrayOf(null, "", "1", "2", "3"),
            )
        }

        val manglendeDynamiskesporsmaalversjon2uke39: ValidationRuleFn = { payload ->
            val rulesetVersion = payload.rulesetVersion
            val sykmeldingPerioder = payload.perioder
            // val utdypendeOpplysinger = payload.utdypendeOpplysninger

            val manglendeDynamiskesporsmaalversjon2uke39 =
                if (rulesetVersion == "2") {
                    sykmeldingPerioder.any { daysBetween(it.fom, it.tom) > 273 }
                    // && !utdypendeOpplysinger.containsAnswersFor(QuestionGroup.GROUP_6_5)
                } else false

            RuleOutput(
                ruleInputs =
                    mapOf(
                        "rulesetVersion" to rulesetVersion,
                        "sykmeldingPerioder" to sykmeldingPerioder,
                        // "utdypendeOpplysninger" to payload.utdypendeOpplysninger,
                    ),
                rule = ValidationRule.MANGLENDE_DYNAMISKE_SPOERSMAL_VERSJON2_UKE_39,
                ruleResult = manglendeDynamiskesporsmaalversjon2uke39,
            )
        }

        val ugyldingOrgNummerLengde: ValidationRuleFn = { payload ->
            val legekontorOrgnr = payload.legekontorOrgnr

            val ugyldingOrgNummerLengde = legekontorOrgnr != null && legekontorOrgnr.length != 9

            RuleOutput(
                ruleInputs = mapOf("ugyldingOrgNummerLengde" to ugyldingOrgNummerLengde),
                rule = ValidationRule.UGYLDIG_ORGNR_LENGDE,
                ruleResult = ugyldingOrgNummerLengde,
            )
        }

        val avsenderSammeSomPasient: ValidationRuleFn = { payload ->
            val avsenderFnr = payload.avsenderFnr
            val patientPersonNumber = payload.patientPersonNumber

            val avsenderSammeSomPasient = avsenderFnr == patientPersonNumber

            RuleOutput(
                ruleInputs =
                    mapOf(
                        "avsenderFnr" to avsenderFnr,
                        "patientPersonNumber" to patientPersonNumber,
                    ),
                rule = ValidationRule.AVSENDER_FNR_ER_SAMME_SOM_PASIENT_FNR,
                ruleResult = avsenderSammeSomPasient,
            )
        }

        val behandlerSammeSomPasient: ValidationRuleFn = { payload ->
            val behandlerFnr = payload.behandlerFnr
            val pasientFodselsNummer = payload.patientPersonNumber

            val behandlerSammeSomPasient = behandlerFnr == pasientFodselsNummer

            RuleOutput(
                ruleInputs =
                    mapOf(
                        "behandlerFnr" to behandlerFnr,
                        "pasientFodselsNummer" to pasientFodselsNummer,
                    ),
                rule = ValidationRule.BEHANDLER_FNR_ER_SAMME_SOM_PASIENT_FNR,
                ruleResult = behandlerSammeSomPasient,
            )
        }
    }

/*
fun Map<String, Map<String, SporsmalSvar>>.containsAnswersFor(questionGroup: QuestionGroup) =
    this[questionGroup.spmGruppeId]?.all { (spmId, _) ->
        spmId in questionGroup.spmsvar.map { it.spmId }
    }
*/
