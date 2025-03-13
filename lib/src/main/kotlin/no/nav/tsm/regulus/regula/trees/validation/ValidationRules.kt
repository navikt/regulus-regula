package no.nav.tsm.regulus.regula.trees.validation

import no.nav.tsm.regulus.regula.dsl.RuleOutput
import no.nav.tsm.regulus.regula.utils.daysBetween

enum class ValidationRules {
    UGYLDIG_REGELSETTVERSJON,
    MANGLENDE_DYNAMISKE_SPOERSMAL_VERSJON2_UKE_39,
    UGYLDIG_ORGNR_LENGDE,
    AVSENDER_FNR_ER_SAMME_SOM_PASIENT_FNR,
    BEHANDLER_FNR_ER_SAMME_SOM_PASIENT_FNR,
}

private typealias ValidationRuleFn = (payload: ValidationRulePayload) -> RuleOutput<ValidationRules>

fun getRule(rules: ValidationRules): ValidationRuleFn = when (rules) {
    ValidationRules.UGYLDIG_REGELSETTVERSJON -> Rules.ugyldigRegelsettversjon
    ValidationRules.MANGLENDE_DYNAMISKE_SPOERSMAL_VERSJON2_UKE_39 -> Rules.manglendeDynamiskesporsmaalversjon2uke39
    ValidationRules.UGYLDIG_ORGNR_LENGDE -> Rules.ugyldingOrgNummerLengde
    ValidationRules.AVSENDER_FNR_ER_SAMME_SOM_PASIENT_FNR -> Rules.avsenderSammeSomPasient
    ValidationRules.BEHANDLER_FNR_ER_SAMME_SOM_PASIENT_FNR -> Rules.behandlerSammeSomPasient
}

private val Rules = object {
    val ugyldigRegelsettversjon: ValidationRuleFn = { payload ->
        val rulesetVersion = payload.rulesetVersion

        RuleOutput(
            ruleInputs = mapOf("rulesetVersion" to rulesetVersion),
            rule = ValidationRules.UGYLDIG_REGELSETTVERSJON,
            ruleResult = rulesetVersion !in arrayOf(null, "", "1", "2", "3"),
        )
    }

    val manglendeDynamiskesporsmaalversjon2uke39: ValidationRuleFn = { payload ->
        val rulesetVersion = payload.rulesetVersion
        val sykmeldingPerioder = payload.perioder
        // val utdypendeOpplysinger = payload.utdypendeOpplysninger

        val manglendeDynamiskesporsmaalversjon2uke39 = if (rulesetVersion == "2") {
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
            rule = ValidationRules.MANGLENDE_DYNAMISKE_SPOERSMAL_VERSJON2_UKE_39,
            ruleResult = manglendeDynamiskesporsmaalversjon2uke39,
        )
    }

    val ugyldingOrgNummerLengde: ValidationRuleFn = { payload ->
        val legekontorOrgnr = payload.legekontorOrgnr

        val ugyldingOrgNummerLengde = legekontorOrgnr != null && legekontorOrgnr.length != 9

        RuleOutput(
            ruleInputs = mapOf("ugyldingOrgNummerLengde" to ugyldingOrgNummerLengde),
            rule = ValidationRules.UGYLDIG_ORGNR_LENGDE,
            ruleResult = ugyldingOrgNummerLengde,
        )
    }

    val avsenderSammeSomPasient: ValidationRuleFn = { payload ->
        val avsenderFnr = payload.avsenderFnr
        val patientPersonNumber = payload.patientPersonNumber

        val avsenderSammeSomPasient = avsenderFnr == patientPersonNumber

        RuleOutput(
            ruleInputs = mapOf(
                "avsenderFnr" to avsenderFnr,
                "patientPersonNumber" to patientPersonNumber,
            ),
            rule = ValidationRules.AVSENDER_FNR_ER_SAMME_SOM_PASIENT_FNR,
            ruleResult = avsenderSammeSomPasient,
        )
    }

    val behandlerSammeSomPasient: ValidationRuleFn = { payload ->
        val behandlerFnr = payload.behandlerFnr
        val pasientFodselsNummer = payload.patientPersonNumber

        val behandlerSammeSomPasient = behandlerFnr == pasientFodselsNummer

        RuleOutput(
            ruleInputs = mapOf(
                "behandlerFnr" to behandlerFnr,
                "pasientFodselsNummer" to pasientFodselsNummer,
            ),
            rule = ValidationRules.BEHANDLER_FNR_ER_SAMME_SOM_PASIENT_FNR,
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
