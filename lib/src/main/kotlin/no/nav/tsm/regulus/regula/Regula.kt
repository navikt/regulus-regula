@file:Suppress("unused") // This is the library entry point

package no.nav.tsm.regulus.regula

import no.nav.tsm.regulus.regula.dsl.RuleStatus
import no.nav.tsm.regulus.regula.dsl.getOutcome
import no.nav.tsm.regulus.regula.dsl.getRulePath
import no.nav.tsm.regulus.regula.dsl.toRegulaJuridisk
import no.nav.tsm.regulus.regula.executor.ExecutionMode
import no.nav.tsm.regulus.regula.executor.TreeExecutor
import no.nav.tsm.regulus.regula.executor.runRules
import no.nav.tsm.regulus.regula.metrics.registerResultMetrics
import no.nav.tsm.regulus.regula.metrics.registerVersionMetrics
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

/**
 * Executes the Regula rules based on the provided [RegulaPayload] and [ExecutionMode].
 *
 * Please follow the documentation for each proprety in the [RegulaPayload] to understand what data
 * is required, and where it should come from.
 */
fun executeRegulaRules(ruleExecutionPayload: RegulaPayload, mode: ExecutionMode): RegulaResult {
    registerVersionMetrics()

    val executedChain =
        runRules(
            sequence =
                createSequence(
                    legeSuspensjonRulePayload = ruleExecutionPayload.toLegeSuspensjonRulePayload(),
                    valideringRulePayload = ruleExecutionPayload.toValideringRulePayload(mode),
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
            .firstOrNull { it != RuleStatus.OK }
            .let {
                when (it) {
                    RuleStatus.INVALID -> RegulaStatus.INVALID
                    RuleStatus.MANUAL_PROCESSING -> RegulaStatus.MANUAL_PROCESSING
                    RuleStatus.OK,
                    null -> RegulaStatus.OK
                }
            }

    val outcome: RegulaOutcome? =
        executedChain
            .map { it.name to it.treeResult.getOutcome() }
            .mapNotNull { (tree, outcome) -> outcome?.let { tree to outcome } }
            .map { (treeName, outcome) ->
                RegulaOutcome(
                    tree = treeName,
                    status = outcome.status.toRegulaOutcomeStatus(),
                    rule = outcome.name,
                    reason =
                        RegulaOutcomeReason(
                            sykmeldt = outcome.messageForUser,
                            sykmelder = outcome.messageForSender,
                        ),
                )
            }
            .firstOrNull()

    val results: List<TreeResult> =
        executedChain.map { tree ->
            TreeResult(
                status = overallStatus,
                outcome =
                    tree.treeResult.getOutcome()?.let { outcome ->
                        RegulaOutcome(
                            tree = tree.name,
                            status = outcome.status.toRegulaOutcomeStatus(),
                            rule = outcome.name,
                            reason =
                                RegulaOutcomeReason(
                                    sykmeldt = outcome.messageForUser,
                                    sykmelder = outcome.messageForSender,
                                ),
                        )
                    },
                rulePath = tree.getRulePath(),
                ruleInputs = tree.ruleInputs,
                juridisk = tree.treeResult.juridisk.toRegulaJuridisk(),
            )
        }

    val regulaResult =
        when (overallStatus) {
            RegulaStatus.OK -> RegulaResult.Ok(results = results)
            RegulaStatus.MANUAL_PROCESSING,
            RegulaStatus.INVALID -> {
                requireNotNull(outcome) {
                    "Outcome should not be null when status is MANUAL_PROCESSING or INVALID. This should not be possible."
                }

                RegulaResult.NotOk(status = overallStatus, outcome = outcome, results = results)
            }
        }

    registerResultMetrics(regulaResult, mode)

    return regulaResult
}

private fun RuleStatus.toRegulaOutcomeStatus(): RegulaOutcomeStatus =
    when (this) {
        RuleStatus.MANUAL_PROCESSING -> RegulaOutcomeStatus.MANUAL_PROCESSING
        RuleStatus.INVALID -> RegulaOutcomeStatus.INVALID
        RuleStatus.OK ->
            throw IllegalStateException(
                "OK status should not be converted to RegulaOutcomeStatus. This should not be possible."
            )
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
