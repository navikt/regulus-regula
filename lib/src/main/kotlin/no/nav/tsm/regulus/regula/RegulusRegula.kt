@file:Suppress("unused") // This is the library entry point

package no.nav.tsm.regulus.regula

import no.nav.tsm.regulus.regula.executor.RuleStatus
import no.nav.tsm.regulus.regula.executor.TreeExecutor
import no.nav.tsm.regulus.regula.executor.runRules
import no.nav.tsm.regulus.regula.trees.arbeidsuforhet.ArbeidsuforhetRulePayload
import no.nav.tsm.regulus.regula.trees.arbeidsuforhet.ArbeidsuforhetRules
import no.nav.tsm.regulus.regula.trees.dato.DatoRulePayload
import no.nav.tsm.regulus.regula.trees.dato.DatoRules
import no.nav.tsm.regulus.regula.trees.hpr.HprRulePayload
import no.nav.tsm.regulus.regula.trees.hpr.HprRules
import no.nav.tsm.regulus.regula.trees.legeSuspensjon.LegeSuspensjonRulePayload
import no.nav.tsm.regulus.regula.trees.legeSuspensjon.LegeSuspensjonRules
import no.nav.tsm.regulus.regula.trees.pasientUnder13.PasientUnder13RulePayload
import no.nav.tsm.regulus.regula.trees.pasientUnder13.PasientUnder13Rules
import no.nav.tsm.regulus.regula.trees.periode.PeriodeRulePayload
import no.nav.tsm.regulus.regula.trees.periode.PeriodeRules
import no.nav.tsm.regulus.regula.trees.tilbakedatering.TilbakedateringRulePayload
import no.nav.tsm.regulus.regula.trees.tilbakedatering.TilbakedateringRules
import no.nav.tsm.regulus.regula.trees.validering.ValideringRulePayload
import no.nav.tsm.regulus.regula.trees.validering.ValideringRules

/** The entire payload needed to apply the rules to this specific sykmelding. */
data class RegulusRegulaPayload(val sykmeldingId: String)

data class RegulusRegulaResult(val good: Boolean)

/** Apply all the rules to the given sykmelding. */
fun executeRules(sykmelding: RegulusRegulaPayload): RegulusRegulaResult {
    val result =
        runRules(
            createSequence(
                legeSuspensjonRulePayload = TODO(),
                valideringRulePayload = TODO(),
                periodeRulePayload = TODO(),
                hprRulePayload = TODO(),
                arbeidsuforhetRulePayload = TODO(),
                pasientUnder13RulePayload = TODO(),
                datoRulePayload = TODO(),
                tilbakedateringRulePayload = TODO(),
            )
        )

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
