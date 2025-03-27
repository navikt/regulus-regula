@file:Suppress("unused") // This is the library entry point

package no.nav.tsm.regulus.regula

import no.nav.tsm.regulus.regula.executor.RuleStatus
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
fun executeRules(sykmelding: RegulusRegulaPayload): RegulusRegulaResult {
    val result =
        runRules(
            createSequence(
                legeSuspensjonRulePayload = sykmelding.toLegeSuspensjonRulePayload(),
                valideringRulePayload = sykmelding.toValideringRulePayload(),
                periodeRulePayload = sykmelding.toPeriodeRulePayload(),
                hprRulePayload = sykmelding.toHprRulePayload(),
                arbeidsuforhetRulePayload = sykmelding.toArbeidsuforhetRulePayload(),
                pasientUnder13RulePayload = sykmelding.toPasientUnder13RulePayload(),
                datoRulePayload = sykmelding.toDatoRulePayload(),
                tilbakedateringRulePayload = sykmelding.toTilbakedateringRulePayload(),
            )
        )

    // TODO: Map to elevated result
    return RegulusRegulaResult(result.first().first.treeResult.status == RuleStatus.OK)
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
