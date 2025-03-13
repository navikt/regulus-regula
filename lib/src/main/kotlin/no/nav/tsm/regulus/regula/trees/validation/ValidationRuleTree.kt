package no.nav.tsm.regulus.regula.trees.validation

import no.nav.tsm.regulus.regula.dsl.RuleNode
import no.nav.tsm.regulus.regula.dsl.UtenJuridisk
import no.nav.tsm.regulus.regula.dsl.tree
import no.nav.tsm.regulus.regula.executor.RuleResult
import no.nav.tsm.regulus.regula.executor.RuleStatus
import no.nav.tsm.regulus.regula.executor.RuleStatus.OK
import no.nav.tsm.regulus.regula.executor.RuleStatus.INVALID

enum class ValidationRules {
    UGYLDIG_REGELSETTVERSJON,
    MANGLENDE_DYNAMISKE_SPOERSMAL_VERSJON2_UKE_39,
    UGYLDIG_ORGNR_LENGDE,
    AVSENDER_FNR_ER_SAMME_SOM_PASIENT_FNR,
    BEHANDLER_FNR_ER_SAMME_SOM_PASIENT_FNR,
}

val validationRuleTree =
    tree<ValidationRules, RuleResult>(ValidationRules.UGYLDIG_REGELSETTVERSJON) {
        yes(INVALID, ValidationRuleHit.UGYLDIG_REGELSETTVERSJON)
        no(ValidationRules.MANGLENDE_DYNAMISKE_SPOERSMAL_VERSJON2_UKE_39) {
            yes(INVALID, ValidationRuleHit.MANGLENDE_DYNAMISKE_SPOERSMAL_VERSJON2_UKE_39)
            no(ValidationRules.UGYLDIG_ORGNR_LENGDE) {
                yes(INVALID, ValidationRuleHit.UGYLDIG_ORGNR_LENGDE)
                no(ValidationRules.AVSENDER_FNR_ER_SAMME_SOM_PASIENT_FNR) {
                    yes(INVALID, ValidationRuleHit.AVSENDER_FNR_ER_SAMME_SOM_PASIENT_FNR)
                    no(ValidationRules.BEHANDLER_FNR_ER_SAMME_SOM_PASIENT_FNR) {
                        yes(INVALID, ValidationRuleHit.BEHANDLER_FNR_ER_SAMME_SOM_PASIENT_FNR)
                        no(OK)
                    }
                }
            }
        }
    } to UtenJuridisk

internal fun RuleNode<ValidationRules, RuleResult>.yes(
    status: RuleStatus,
    ruleHit: ValidationRuleHit? = null
) {
    yes(RuleResult(status, ruleHit?.ruleHit))
}

internal fun RuleNode<ValidationRules, RuleResult>.no(
    status: RuleStatus,
    ruleHit: ValidationRuleHit? = null
) {
    no(RuleResult(status, ruleHit?.ruleHit))
}

fun getRule(rules: ValidationRules): Rule<ValidationRules> {
    return when (rules) {
        ValidationRules.UGYLDIG_REGELSETTVERSJON -> ugyldigRegelsettversjon
        ValidationRules.MANGLENDE_DYNAMISKE_SPOERSMAL_VERSJON2_UKE_39 ->
            manglendeDynamiskesporsmaalversjon2uke39
        ValidationRules.UGYLDIG_ORGNR_LENGDE -> ugyldingOrgNummerLengde
        ValidationRules.AVSENDER_FNR_ER_SAMME_SOM_PASIENT_FNR -> avsenderSammeSomPasient
        ValidationRules.BEHANDLER_FNR_ER_SAMME_SOM_PASIENT_FNR -> behandlerSammeSomPasient
    }
}
