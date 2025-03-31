package no.nav.tsm.regulus.regula.rules.trees.legeSuspensjon

import no.nav.tsm.regulus.regula.dsl.RuleJuridisk
import no.nav.tsm.regulus.regula.dsl.TreeNode.LeafNode.*
import no.nav.tsm.regulus.regula.dsl.tree

internal val legeSuspensjonRuleTree =
    tree(LegeSuspensjonRule.BEHANDLER_SUSPENDERT) {
        yes(INVALID(LegeSuspensjonRule.Outcomes.BEHANDLER_SUSPENDERT, RuleJuridisk.INGEN))
        no(OK(RuleJuridisk.INGEN))
    }
