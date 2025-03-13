package no.nav.tsm.regulus.regula.trees.legesuspensjon

import no.nav.tsm.regulus.regula.dsl.UtenJuridisk
import no.nav.tsm.regulus.regula.dsl.tree
import no.nav.tsm.regulus.regula.executor.RuleResult
import no.nav.tsm.regulus.regula.executor.RuleStatus
import no.nav.tsm.regulus.regula.executor.no
import no.nav.tsm.regulus.regula.executor.yes

val legeSuspensjonRuleTree = tree<LegeSuspensjonRules, RuleResult>(
    LegeSuspensjonRules.BEHANDLER_SUSPENDERT
) {
    yes(RuleStatus.INVALID, LegeSuspensjonRuleOutcomes.BEHANDLER_SUSPENDERT)
    no(RuleStatus.OK)
} to UtenJuridisk
