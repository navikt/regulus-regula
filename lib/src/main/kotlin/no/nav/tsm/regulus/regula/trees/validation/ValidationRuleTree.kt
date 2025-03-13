package no.nav.tsm.regulus.regula.trees.validation

import no.nav.tsm.regulus.regula.dsl.UtenJuridisk
import no.nav.tsm.regulus.regula.dsl.tree
import no.nav.tsm.regulus.regula.executor.RuleResult
import no.nav.tsm.regulus.regula.executor.RuleStatus.OK
import no.nav.tsm.regulus.regula.executor.RuleStatus.INVALID
import no.nav.tsm.regulus.regula.executor.no
import no.nav.tsm.regulus.regula.executor.yes

val validationRuleTree =
    tree<ValidationRules, RuleResult>(ValidationRules.UGYLDIG_REGELSETTVERSJON) {
        yes(INVALID, ValidationRuleOutcomes.UGYLDIG_REGELSETTVERSJON)
        no(ValidationRules.MANGLENDE_DYNAMISKE_SPOERSMAL_VERSJON2_UKE_39) {
            yes(INVALID, ValidationRuleOutcomes.MANGLENDE_DYNAMISKE_SPOERSMAL_VERSJON2_UKE_39)
            no(ValidationRules.UGYLDIG_ORGNR_LENGDE) {
                yes(INVALID, ValidationRuleOutcomes.UGYLDIG_ORGNR_LENGDE)
                no(ValidationRules.AVSENDER_FNR_ER_SAMME_SOM_PASIENT_FNR) {
                    yes(INVALID, ValidationRuleOutcomes.AVSENDER_FNR_ER_SAMME_SOM_PASIENT_FNR)
                    no(ValidationRules.BEHANDLER_FNR_ER_SAMME_SOM_PASIENT_FNR) {
                        yes(INVALID, ValidationRuleOutcomes.BEHANDLER_FNR_ER_SAMME_SOM_PASIENT_FNR)
                        no(OK)
                    }
                }
            }
        }
    } to UtenJuridisk
