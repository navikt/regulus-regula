package no.nav.tsm.regulus.regula

import java.time.LocalDateTime
import no.nav.tsm.regulus.regula.dsl.Juridisk
import no.nav.tsm.regulus.regula.dsl.TreeOutput
import no.nav.tsm.regulus.regula.dsl.printRulePath
import no.nav.tsm.regulus.regula.executor.RuleResult
import no.nav.tsm.regulus.regula.executor.RuleStatus
import no.nav.tsm.regulus.regula.executor.TreeExecutor
import no.nav.tsm.regulus.regula.trees.hpr.Behandler
import no.nav.tsm.regulus.regula.trees.hpr.HprRulePayload
import no.nav.tsm.regulus.regula.trees.hpr.HprRulesExecution
import no.nav.tsm.regulus.regula.trees.legesuspensjon.LegeSuspensjonPayload
import no.nav.tsm.regulus.regula.trees.legesuspensjon.LegeSuspensjonRulesExecution
import no.nav.tsm.regulus.regula.trees.validation.ValidationRulePayload
import no.nav.tsm.regulus.regula.trees.validation.ValidationRulesExecutor

typealias RuleExecutionResult = List<Pair<TreeOutput<*, RuleResult>, Juridisk>>

fun runSykmeldingRules(sykmeldingId: String): RuleExecutionResult {
    // Dummy rule sequence for testing, TODO is to create an lib API that exposes this to consumers
    // of the library with proper input/output types

    val ruleSequence =
        sequenceOf(
            LegeSuspensjonRulesExecution(LegeSuspensjonPayload(sykmeldingId, false)),
            ValidationRulesExecutor(
                ValidationRulePayload(
                    sykmeldingId,
                    rulesetVersion = "2",
                    perioder = emptyList(),
                    legekontorOrgnr = "123",
                    behandlerFnr = "08201023912",
                    avsenderFnr = "01912391932",
                    patientPersonNumber = "92102931803",
                )
            ),
            // PeriodLogicRulesExecution(periodLogicRuleTree),
            HprRulesExecution(
                HprRulePayload(
                    sykmeldingId = sykmeldingId,
                    behandler = Behandler(godkjenninger = emptyList()),
                    perioder = emptyList(),
                    startdato = null,
                    signaturDato = LocalDateTime.now(),
                )
            ),
            // ArbeidsuforhetRulesExecution(arbeidsuforhetRuleTree),
            // PatientAgeUnder13RulesExecution(patientAgeUnder13RuleTree),
            // PeriodeRulesExecution(periodeRuleTree),
            // TilbakedateringRulesExecution(tilbakedateringRuleTree),
        )

    return runRules(ruleSequence)
}

private fun runRules(sequence: Sequence<TreeExecutor<*, *>>): RuleExecutionResult {
    var lastStatus = RuleStatus.OK
    val results =
        sequence
            .map { it.execute() }
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

    results.forEach { println(it.first.printRulePath()) }
}
