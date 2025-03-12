package no.nav.tsm.regulus.regula


import no.nav.tsm.regulus.regula.dsl.Juridisk
import no.nav.tsm.regulus.regula.dsl.TreeOutput
import no.nav.tsm.regulus.regula.executor.RuleExecution
import no.nav.tsm.regulus.regula.executor.RuleResult
import no.nav.tsm.regulus.regula.executor.RuleStatus
import no.nav.tsm.regulus.regula.trees.legesuspensjon.LegeSuspensjonRulesExecution

class RuleExecutor() {
    private val ruleExecution =
        sequenceOf(
            LegeSuspensjonRulesExecution(
                "What", true
            ),
            // ValidationRulesExecution(validationRuleTree),
            // PeriodLogicRulesExecution(periodLogicRuleTree),
            // HPRRulesExecution(hprRuleTree),
            // ArbeidsuforhetRulesExecution(arbeidsuforhetRuleTree),
            // PatientAgeUnder13RulesExecution(patientAgeUnder13RuleTree),
            // PeriodeRulesExecution(periodeRuleTree),
            // TilbakedateringRulesExecution(tilbakedateringRuleTree),
        )

    fun runRules(
        sequence: Sequence<RuleExecution<out Enum<*>>> = ruleExecution,
    ): List<Pair<TreeOutput<out Enum<*>, RuleResult>, Juridisk>> {
        var lastStatus = RuleStatus.OK
        val results =
            sequence
                .map { it.runRules() }
                .takeWhile {
                    if (lastStatus == RuleStatus.OK) {
                        lastStatus = it.first.treeResult.status
                        true
                    } else {
                        false
                    }
                }
        return results.toList()
    }
}
