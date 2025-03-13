package no.nav.tsm.regulus.regula


import no.nav.tsm.regulus.regula.dsl.Juridisk
import no.nav.tsm.regulus.regula.dsl.TreeOutput
import no.nav.tsm.regulus.regula.executor.RuleExecution
import no.nav.tsm.regulus.regula.executor.RuleResult
import no.nav.tsm.regulus.regula.executor.RuleStatus
import no.nav.tsm.regulus.regula.trees.legesuspensjon.LegeSuspensjonRulesExecution
import no.nav.tsm.regulus.regula.trees.validation.ValidationRulePayload
import no.nav.tsm.regulus.regula.trees.validation.ValidationRulesExecution

class RuleExecutor() {
    private val ruleExecution =
        sequenceOf(
            LegeSuspensjonRulesExecution(
                "What", true
            ),
            ValidationRulesExecution(
                "What", ValidationRulePayload(
                    rulesetVersion = "2",
                    perioder = emptyList(),
                    legekontorOrgnr = "123",
                    behandlerFnr = "08201023912",
                    avsenderFnr = "01912391932",
                    patientPersonNumber = "92102931803"

                )
            ),
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
