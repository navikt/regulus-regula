package no.nav.tsm.regulus.regula.rules.trees.validering

import no.nav.tsm.regulus.regula.dsl.RuleStatus.INVALID
import no.nav.tsm.regulus.regula.dsl.RuleStatus.OK
import no.nav.tsm.regulus.regula.dsl.no
import no.nav.tsm.regulus.regula.dsl.tree
import no.nav.tsm.regulus.regula.dsl.yes
import no.nav.tsm.regulus.regula.juridisk.UtenJuridisk

internal val valideringRuleTree =
    tree<ValideringRule>(ValideringRule.UGYLDIG_REGELSETTVERSJON) {
        yes(INVALID, ValideringRule.Outcomes.UGYLDIG_REGELSETTVERSJON)
        no(ValideringRule.MANGLENDE_DYNAMISKE_SPOERSMAL_VERSJON2_UKE_39) {
            yes(INVALID, ValideringRule.Outcomes.MANGLENDE_DYNAMISKE_SPOERSMAL_VERSJON2_UKE_39)
            no(ValideringRule.UGYLDIG_ORGNR_LENGDE) {
                yes(INVALID, ValideringRule.Outcomes.UGYLDIG_ORGNR_LENGDE)
                no(ValideringRule.AVSENDER_FNR_ER_SAMME_SOM_PASIENT_FNR) {
                    yes(INVALID, ValideringRule.Outcomes.AVSENDER_FNR_ER_SAMME_SOM_PASIENT_FNR)
                    no(ValideringRule.BEHANDLER_FNR_ER_SAMME_SOM_PASIENT_FNR) {
                        yes(INVALID, ValideringRule.Outcomes.BEHANDLER_FNR_ER_SAMME_SOM_PASIENT_FNR)
                        no(OK)
                    }
                }
            }
        }
    } to UtenJuridisk
