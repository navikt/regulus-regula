package no.nav.tsm.regulus.regula.rules.trees.validering

import no.nav.tsm.regulus.regula.dsl.RuleJuridisk
import no.nav.tsm.regulus.regula.dsl.RuleOutcome
import no.nav.tsm.regulus.regula.dsl.TreeNode.LeafNode
import no.nav.tsm.regulus.regula.dsl.tree

internal val valideringRuleTree =
    tree(ValideringRule.UGYLDIG_ORGNR_LENGDE) {
        yes(INVALID(ValideringRule.Outcomes.UGYLDIG_ORGNR_LENGDE))
        no(ValideringRule.PAPIRSYKMELDING) {
            yes(OK())
            no(ValideringRule.UGYLDIG_REGELSETTVERSJON) {
                yes(INVALID(ValideringRule.Outcomes.UGYLDIG_REGELSETTVERSJON))
                no(ValideringRule.MANGLENDE_DYNAMISKE_SPOERSMAL_VERSJON2_UKE_39) {
                    yes(
                        INVALID(
                            ValideringRule.Outcomes.MANGLENDE_DYNAMISKE_SPOERSMAL_VERSJON2_UKE_39
                        )
                    )
                    no(ValideringRule.AVSENDER_FNR_ER_SAMME_SOM_PASIENT_FNR) {
                        yes(INVALID(ValideringRule.Outcomes.AVSENDER_FNR_ER_SAMME_SOM_PASIENT_FNR))
                        no(ValideringRule.SYKMELDER_FNR_ER_SAMME_SOM_PASIENT_FNR) {
                            yes(
                                INVALID(
                                    ValideringRule.Outcomes.SYKMELDER_FNR_ER_SAMME_SOM_PASIENT_FNR
                                )
                            )
                            no(OK())
                        }
                    }
                }
            }
        }
    }

private fun INVALID(outcome: RuleOutcome): LeafNode.INVALID<ValideringRule> =
    LeafNode.INVALID(outcome, RuleJuridisk.INGEN)

private fun OK(): LeafNode.OK<ValideringRule> = LeafNode.OK(RuleJuridisk.INGEN)
