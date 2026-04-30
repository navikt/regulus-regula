package no.nav.tsm.regulus.regula.rules.trees.dato

import no.nav.tsm.regulus.regula.dsl.RuleJuridisk
import no.nav.tsm.regulus.regula.dsl.TreeNode.LeafNode.*
import no.nav.tsm.regulus.regula.dsl.tree

internal val datoRuleTree =
    tree(DatoRule.FREMDATERT) {
        yes(INVALID(DatoRule.Outcomes.FREMDATERT, RuleJuridisk.INGEN))
        no(DatoRule.TILBAKEDATERT_MER_ENN_3_AR) {
            yes(INVALID(DatoRule.Outcomes.TILBAKEDATERT_MER_ENN_3_AR, RuleJuridisk.INGEN))
            no(DatoRule.TOTAL_VARIGHET_OVER_ETT_AAR) {
                yes(INVALID(DatoRule.Outcomes.TOTAL_VARIGHET_OVER_ETT_AAR, RuleJuridisk.INGEN))
                no(OK(RuleJuridisk.INGEN))
            }
        }
    }
