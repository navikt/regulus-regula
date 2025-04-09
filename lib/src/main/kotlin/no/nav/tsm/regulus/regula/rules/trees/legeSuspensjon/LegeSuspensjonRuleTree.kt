package no.nav.tsm.regulus.regula.rules.trees.legeSuspensjon

import no.nav.tsm.regulus.regula.dsl.RuleJuridisk
import no.nav.tsm.regulus.regula.dsl.TreeNode.LeafNode.*
import no.nav.tsm.regulus.regula.dsl.tree

internal val legeSuspensjonRuleTree =
    tree(LegeSuspensjonRule.SYKMELDER_SUSPENDERT) {
        yes(INVALID(LegeSuspensjonRule.Outcomes.SYKMELDER_SUSPENDERT, RuleJuridisk.INGEN))
        no(OK(RuleJuridisk.INGEN))
    }
