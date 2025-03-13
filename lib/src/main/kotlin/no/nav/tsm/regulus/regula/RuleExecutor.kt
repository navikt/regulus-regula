package no.nav.tsm.regulus.regula


import no.nav.tsm.regulus.regula.dsl.Juridisk
import no.nav.tsm.regulus.regula.dsl.TreeOutput
import no.nav.tsm.regulus.regula.dsl.printRulePath
import no.nav.tsm.regulus.regula.executor.RuleExecution
import no.nav.tsm.regulus.regula.executor.RuleResult
import no.nav.tsm.regulus.regula.executor.RuleStatus
import no.nav.tsm.regulus.regula.trees.legesuspensjon.LegeSuspensjonRulesExecution
import no.nav.tsm.regulus.regula.trees.validation.ValidationRulePayload
import no.nav.tsm.regulus.regula.trees.validation.ValidationRulesExecution

typealias RuleExecutionResult = List<Pair<TreeOutput<out Enum<*>, RuleResult>, Juridisk>>

fun runSykmeldingRules(
    sykmeldingId: String
): RuleExecutionResult {
    val ruleSequence = sequenceOf(
        LegeSuspensjonRulesExecution(
            sykmeldingId, false
        ),
        ValidationRulesExecution(
            sykmeldingId, ValidationRulePayload(
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


    return runRules(ruleSequence)
}

private fun runRules(
    sequence: Sequence<RuleExecution<out Enum<*>>>,
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


// TODO: Only for dev
fun main() {
    val results = runSykmeldingRules("123")

    results.forEach {
        println(it.first.printRulePath())
    }
}
