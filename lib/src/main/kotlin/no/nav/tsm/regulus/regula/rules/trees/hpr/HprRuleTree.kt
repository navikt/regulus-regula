package no.nav.tsm.regulus.regula.rules.trees.hpr

import no.nav.tsm.regulus.regula.dsl.RuleJuridisk
import no.nav.tsm.regulus.regula.dsl.RuleOutcome
import no.nav.tsm.regulus.regula.dsl.TreeNode.LeafNode
import no.nav.tsm.regulus.regula.dsl.TreeNode.RuleNode
import no.nav.tsm.regulus.regula.dsl.tree

internal val hprRuleTree =
    tree(HprRule.BEHANDLER_GYLIDG_I_HPR) {
        no(INVALID(HprRule.Outcomes.BEHANDLER_IKKE_GYLDIG_I_HPR))
        yes(HprRule.BEHANDLER_HAR_AUTORISASJON_I_HPR) {
            no(INVALID(HprRule.Outcomes.BEHANDLER_MANGLER_AUTORISASJON_I_HPR))
            yes(HprRule.BEHANDLER_ER_LEGE_I_HPR) {
                yes(OK())
                no(HprRule.BEHANDLER_ER_TANNLEGE_I_HPR) {
                    yes(OK())
                    no(HprRule.BEHANDLER_ER_MANUELLTERAPEUT_I_HPR) {
                        yesThenSykefravarOver12Uker()
                        no(HprRule.BEHANDLER_ER_FT_MED_TILLEGSKOMPETANSE_I_HPR) {
                            yesThenSykefravarOver12Uker()
                            no(HprRule.BEHANDLER_ER_KI_MED_TILLEGSKOMPETANSE_I_HPR) {
                                yesThenSykefravarOver12Uker()
                                no(INVALID(HprRule.Outcomes.BEHANDLER_IKKE_LE_KI_MT_TL_FT_I_HPR))
                            }
                        }
                    }
                }
            }
        }
    }

private fun RuleNode<HprRule>.yesThenSykefravarOver12Uker() {
    yes(HprRule.SYKEFRAVAR_OVER_12_UKER) {
        yes(INVALID(HprRule.Outcomes.BEHANDLER_MT_FT_KI_OVER_12_UKER))
        no(OK())
    }
}

private fun INVALID(outcome: RuleOutcome): LeafNode.INVALID<HprRule> =
    LeafNode.INVALID(outcome, RuleJuridisk.FOLKETRYGDLOVEN_8_7_1)

private fun OK(): LeafNode.OK<HprRule> = LeafNode.OK(RuleJuridisk.FOLKETRYGDLOVEN_8_7_1)
