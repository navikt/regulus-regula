@file:Suppress("unused") // This is the library entry point

package no.nav.tsm.regulus.regula

import no.nav.tsm.regulus.regula.dsl.RuleStatus
import no.nav.tsm.regulus.regula.dsl.getOutcome
import no.nav.tsm.regulus.regula.dsl.getRulePath
import no.nav.tsm.regulus.regula.executor.ExecutionMode
import no.nav.tsm.regulus.regula.executor.TreeExecutor
import no.nav.tsm.regulus.regula.executor.runRules
import no.nav.tsm.regulus.regula.rules.trees.arbeidsuforhet.ArbeidsuforhetRulePayload
import no.nav.tsm.regulus.regula.rules.trees.arbeidsuforhet.ArbeidsuforhetRules
import no.nav.tsm.regulus.regula.rules.trees.dato.DatoRulePayload
import no.nav.tsm.regulus.regula.rules.trees.dato.DatoRules
import no.nav.tsm.regulus.regula.rules.trees.hpr.HprRulePayload
import no.nav.tsm.regulus.regula.rules.trees.hpr.HprRules
import no.nav.tsm.regulus.regula.rules.trees.legeSuspensjon.LegeSuspensjonRulePayload
import no.nav.tsm.regulus.regula.rules.trees.legeSuspensjon.LegeSuspensjonRules
import no.nav.tsm.regulus.regula.rules.trees.pasientUnder13.PasientUnder13RulePayload
import no.nav.tsm.regulus.regula.rules.trees.pasientUnder13.PasientUnder13Rules
import no.nav.tsm.regulus.regula.rules.trees.periode.PeriodeRulePayload
import no.nav.tsm.regulus.regula.rules.trees.periode.PeriodeRules
import no.nav.tsm.regulus.regula.rules.trees.tilbakedatering.TilbakedateringRulePayload
import no.nav.tsm.regulus.regula.rules.trees.tilbakedatering.TilbakedateringRules
import no.nav.tsm.regulus.regula.rules.trees.validering.ValideringRulePayload
import no.nav.tsm.regulus.regula.rules.trees.validering.ValideringRules

/** Apply all the rules to the given sykmelding. */
fun executeRegulaRules(ruleExecutionPayload: RegulaPayload, mode: ExecutionMode): RegulaResult {
    val executedChain =
        runRules(
            sequence =
                createSequence(
                    legeSuspensjonRulePayload = ruleExecutionPayload.toLegeSuspensjonRulePayload(),
                    valideringRulePayload = ruleExecutionPayload.toValideringRulePayload(),
                    periodeRulePayload = ruleExecutionPayload.toPeriodeRulePayload(),
                    hprRulePayload = ruleExecutionPayload.toHprRulePayload(),
                    arbeidsuforhetRulePayload = ruleExecutionPayload.toArbeidsuforhetRulePayload(),
                    pasientUnder13RulePayload = ruleExecutionPayload.toPasientUnder13RulePayload(),
                    datoRulePayload = ruleExecutionPayload.toDatoRulePayload(),
                    tilbakedateringRulePayload = ruleExecutionPayload.toTilbakedateringRulePayload(),
                ),
            mode = mode,
        )

    val overallStatus: RegulaStatus =
        executedChain
            .map { it.treeResult.status }
            .let {
                it.firstOrNull { status -> status == RuleStatus.INVALID }
                    ?: it.firstOrNull { status -> status == RuleStatus.MANUAL_PROCESSING }
                    ?: RuleStatus.OK
            }
            .toRegulaStatus()

    val ruleHits: List<RegulaOutcome> =
        executedChain
            .mapNotNull { it.treeResult.getOutcome() }
            .map {
                RegulaOutcome(
                    status = it.status.toRegulaStatus(),
                    rule = it.rule,
                    messageForSender = it.messageForSender,
                    messageForUser = it.messageForUser,
                )
            }

    val results: List<TreeResult> =
        executedChain.map {
            TreeResult(
                outcome =
                    it.treeResult.getOutcome()?.let { outcome ->
                        RegulaOutcome(
                            status = outcome.status.toRegulaStatus(),
                            rule = outcome.rule,
                            messageForSender = outcome.messageForSender,
                            messageForUser = outcome.messageForUser,
                        )
                    },
                rulePath = it.getRulePath(),
            )
        }

    return RegulaResult(status = overallStatus, ruleHits = ruleHits, results = results)
}

private fun RuleStatus.toRegulaStatus(): RegulaStatus =
    when (this) {
        RuleStatus.OK -> RegulaStatus.OK
        RuleStatus.MANUAL_PROCESSING -> RegulaStatus.MANUAL_PROCESSING
        RuleStatus.INVALID -> RegulaStatus.INVALID
    }

/**
 * Defines the order of rule execution. Rules are short-circuited, meaning that if a rule fails, the
 * following rules are not executed. This makes the sequence of execution important.
 */
private fun createSequence(
    legeSuspensjonRulePayload: LegeSuspensjonRulePayload,
    valideringRulePayload: ValideringRulePayload,
    periodeRulePayload: PeriodeRulePayload,
    hprRulePayload: HprRulePayload,
    arbeidsuforhetRulePayload: ArbeidsuforhetRulePayload,
    pasientUnder13RulePayload: PasientUnder13RulePayload,
    datoRulePayload: DatoRulePayload,
    tilbakedateringRulePayload: TilbakedateringRulePayload,
): Sequence<TreeExecutor<*, *>> =
    sequenceOf(
        LegeSuspensjonRules(legeSuspensjonRulePayload),
        ValideringRules(valideringRulePayload),
        PeriodeRules(periodeRulePayload),
        HprRules(hprRulePayload),
        ArbeidsuforhetRules(arbeidsuforhetRulePayload),
        PasientUnder13Rules(pasientUnder13RulePayload),
        DatoRules(datoRulePayload),
        TilbakedateringRules(tilbakedateringRulePayload),
    )
