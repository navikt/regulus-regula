package no.nav.tsm.regulus.regula.rules.trees.arbeidsuforhet

import no.nav.tsm.regulus.regula.dsl.RuleJuridisk
import no.nav.tsm.regulus.regula.dsl.RuleOutcome
import no.nav.tsm.regulus.regula.dsl.TreeNode.LeafNode
import no.nav.tsm.regulus.regula.dsl.tree

/** All juridisk henvisning refer the same henvisning in this tree. */
internal val arbeidsuforhetRuleTree =
    tree(ArbeidsuforhetRule.HOVEDDIAGNOSE_MANGLER) {
        yes(ArbeidsuforhetRule.FRAVAERSGRUNN_MANGLER) {
            yes(INVALID(ArbeidsuforhetRule.Outcomes.FRAVAERSGRUNN_MANGLER))
            no(ArbeidsuforhetRule.UGYLDIG_KODEVERK_FOR_BIDIAGNOSE) {
                yes(INVALID(ArbeidsuforhetRule.Outcomes.UGYLDIG_KODEVERK_FOR_BIDIAGNOSE))
                no(OK())
            }
        }
        no(ArbeidsuforhetRule.UGYLDIG_KODEVERK_FOR_HOVEDDIAGNOSE) {
            yes(INVALID(ArbeidsuforhetRule.Outcomes.UGYLDIG_KODEVERK_FOR_HOVEDDIAGNOSE))
            no(ArbeidsuforhetRule.ICPC_2_Z_DIAGNOSE) {
                yes(INVALID(ArbeidsuforhetRule.Outcomes.ICPC_2_Z_DIAGNOSE))
                no(ArbeidsuforhetRule.UGYLDIG_KODEVERK_FOR_BIDIAGNOSE) {
                    yes(INVALID(ArbeidsuforhetRule.Outcomes.UGYLDIG_KODEVERK_FOR_BIDIAGNOSE))
                    no(OK())
                }
            }
        }
    }

private fun INVALID(outcome: RuleOutcome): LeafNode.INVALID<ArbeidsuforhetRule> =
    LeafNode.INVALID(outcome, RuleJuridisk.FOLKETRYGDLOVEN_8_4)

private fun OK(): LeafNode.OK<ArbeidsuforhetRule> = LeafNode.OK(RuleJuridisk.FOLKETRYGDLOVEN_8_4)
