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
        yes(INVALID, ValidationRule.UGYLDIG_REGELSETTVERSJON.Outcome)
        no(ValidationRule.MANGLENDE_DYNAMISKE_SPOERSMAL_VERSJON2_UKE_39) {
            yes(INVALID, ValidationRule.MANGLENDE_DYNAMISKE_SPOERSMAL_VERSJON2_UKE_39.Outcome)
            no(ValidationRule.UGYLDIG_ORGNR_LENGDE) {
                yes(INVALID, ValidationRule.UGYLDIG_ORGNR_LENGDE.Outcome)
                no(ValidationRule.AVSENDER_FNR_ER_SAMME_SOM_PASIENT_FNR) {
                    yes(INVALID, ValidationRule.AVSENDER_FNR_ER_SAMME_SOM_PASIENT_FNR.Outcome)
                    no(ValidationRule.BEHANDLER_FNR_ER_SAMME_SOM_PASIENT_FNR) {
                        yes(INVALID, ValidationRule.BEHANDLER_FNR_ER_SAMME_SOM_PASIENT_FNR.Outcome)
                        no(OK)
                    }
                }
            }
        }
    } to UtenJuridisk
