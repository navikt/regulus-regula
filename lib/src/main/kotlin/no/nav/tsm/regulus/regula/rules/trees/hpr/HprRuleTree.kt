package no.nav.tsm.regulus.regula.rules.trees.hpr

import no.nav.tsm.regulus.regula.dsl.RuleJuridisk
import no.nav.tsm.regulus.regula.dsl.RuleOutcome
import no.nav.tsm.regulus.regula.dsl.TreeNode.LeafNode
import no.nav.tsm.regulus.regula.dsl.TreeNode.RuleNode
import no.nav.tsm.regulus.regula.dsl.tree

internal val hprRuleTree =
    tree(HprRule.SYKMELDER_FINNES_I_HPR) {
        no(INVALID(HprRule.Outcomes.SYKMELDER_IKKE_I_HPR))
        yes(HprRule.SYKMELDER_GYLDIG_I_HPR) {
            no(INVALID(HprRule.Outcomes.SYKMELDER_IKKE_GYLDIG_I_HPR))
            yes(HprRule.SYKMELDER_HAR_AUTORISASJON_I_HPR) {
                no(INVALID(HprRule.Outcomes.SYKMELDER_MANGLER_AUTORISASJON_I_HPR))
                yes(HprRule.SYKMELDER_ER_LEGE_I_HPR) {
                    yes(OK())
                    no(HprRule.SYKMELDER_ER_TANNLEGE_I_HPR) {
                        yes(OK())
                        no(HprRule.SYKMELDER_ER_MANUELLTERAPEUT_I_HPR) {
                            yesThenSykefravarOver12Uker()
                            no(HprRule.SYKMELDER_ER_FT_MED_TILLEGSKOMPETANSE_I_HPR) {
                                yesThenSykefravarOver12Uker()
                                no(HprRule.SYKMELDER_ER_KI_MED_TILLEGSKOMPETANSE_I_HPR) {
                                    yesThenSykefravarOver12Uker()
                                    no(
                                        INVALID(
                                            HprRule.Outcomes.SYKMELDER_IKKE_LE_KI_MT_TL_FT_I_HPR
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

private fun RuleNode<HprRule>.yesThenSykefravarOver12Uker() {
    yes(HprRule.SYKEFRAVAER_OVER_12_UKER) {
        yes(INVALID(HprRule.Outcomes.SYKMELDER_MT_FT_KI_OVER_12_UKER))
        no(OK())
    }
}

private fun INVALID(outcome: RuleOutcome): LeafNode.INVALID<HprRule> =
    LeafNode.INVALID(outcome, RuleJuridisk.FOLKETRYGDLOVEN_8_7_1)

private fun OK(): LeafNode.OK<HprRule> = LeafNode.OK(RuleJuridisk.FOLKETRYGDLOVEN_8_7_1)
