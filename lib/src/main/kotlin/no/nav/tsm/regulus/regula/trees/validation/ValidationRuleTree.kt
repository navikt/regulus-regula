package no.nav.tsm.regulus.regula.trees.validation

import no.nav.tsm.regulus.regula.dsl.UtenJuridisk
import no.nav.tsm.regulus.regula.dsl.tree
import no.nav.tsm.regulus.regula.executor.RuleResult
import no.nav.tsm.regulus.regula.executor.RuleStatus.OK
import no.nav.tsm.regulus.regula.executor.RuleStatus.INVALID
import no.nav.tsm.regulus.regula.executor.no
import no.nav.tsm.regulus.regula.executor.yes

val validationRuleTree =
    tree<ValidationRule, RuleResult>(ValidationRule.UGYLDIG_REGELSETTVERSJON) {
        yes(INVALID, ValidationRule.Outcomes.UGYLDIG_REGELSETTVERSJON)
        no(ValidationRule.MANGLENDE_DYNAMISKE_SPOERSMAL_VERSJON2_UKE_39) {
            yes(INVALID, ValidationRule.Outcomes.MANGLENDE_DYNAMISKE_SPOERSMAL_VERSJON2_UKE_39)
            no(ValidationRule.UGYLDIG_ORGNR_LENGDE) {
                yes(INVALID, ValidationRule.Outcomes.UGYLDIG_ORGNR_LENGDE)
                no(ValidationRule.AVSENDER_FNR_ER_SAMME_SOM_PASIENT_FNR) {
                    yes(INVALID, ValidationRule.Outcomes.AVSENDER_FNR_ER_SAMME_SOM_PASIENT_FNR)
                    no(ValidationRule.BEHANDLER_FNR_ER_SAMME_SOM_PASIENT_FNR) {
                        yes(INVALID, ValidationRule.Outcomes.BEHANDLER_FNR_ER_SAMME_SOM_PASIENT_FNR)
                        no(OK)
                    }
                }
            }
        }
    } to UtenJuridisk
