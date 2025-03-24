package no.nav.tsm.regulus.regula

import java.time.LocalDateTime
import no.nav.tsm.regulus.regula.dsl.printRulePath
import no.nav.tsm.regulus.regula.executor.RuleExecutionResult
import no.nav.tsm.regulus.regula.executor.runRules
import no.nav.tsm.regulus.regula.trees.arbeidsuforhet.ArbeidsuforhetRulePayload
import no.nav.tsm.regulus.regula.trees.arbeidsuforhet.ArbeidsuforhetRules
import no.nav.tsm.regulus.regula.trees.hpr.Behandler
import no.nav.tsm.regulus.regula.trees.hpr.HprRulePayload
import no.nav.tsm.regulus.regula.trees.hpr.HprRules
import no.nav.tsm.regulus.regula.trees.legesuspensjon.LegeSuspensjonPayload
import no.nav.tsm.regulus.regula.trees.legesuspensjon.LegeSuspensjonRules
import no.nav.tsm.regulus.regula.trees.pasientunder13.PasientUnder13RulePayload
import no.nav.tsm.regulus.regula.trees.pasientunder13.PasientUnder13Rules
import no.nav.tsm.regulus.regula.trees.periode.PeriodeRulePayload
import no.nav.tsm.regulus.regula.trees.periode.PeriodeRules
import no.nav.tsm.regulus.regula.trees.periodvalidering.PeriodLogicRulePayload
import no.nav.tsm.regulus.regula.trees.periodvalidering.PeriodLogicRules
import no.nav.tsm.regulus.regula.trees.tilbakedatering.TilbakedateringRulePayload
import no.nav.tsm.regulus.regula.trees.tilbakedatering.TilbakedateringRules
import no.nav.tsm.regulus.regula.trees.validation.ValidationRulePayload
import no.nav.tsm.regulus.regula.trees.validation.ValidationRules

fun runSykmeldingRules(sykmeldingId: String): RuleExecutionResult {
    // Dummy rule sequence for testing, TODO is to create an lib API that exposes this to consumers
    // of the library with proper input/output types

    val ruleSequence =
        sequenceOf(
            LegeSuspensjonRules(LegeSuspensjonPayload(sykmeldingId, false)),
            ValidationRules(
                ValidationRulePayload(
                    sykmeldingId,
                    rulesetVersion = "2",
                    perioder = emptyList(),
                    legekontorOrgnr = "123",
                    behandlerFnr = "08201023912",
                    avsenderFnr = "01912391932",
                    pasientIdent = "92102931803",
                    utdypendeOpplysninger = emptyMap(),
                )
            ),
            PeriodLogicRules(
                PeriodLogicRulePayload(
                    sykmeldingId,
                    perioder = emptyList(),
                    behandletTidspunkt = LocalDateTime.now(),
                    receivedDate = LocalDateTime.now(),
                )
            ),
            HprRules(
                HprRulePayload(
                    sykmeldingId = sykmeldingId,
                    behandler = Behandler(godkjenninger = emptyList()),
                    perioder = emptyList(),
                    startdato = null,
                    signaturdato = LocalDateTime.now(),
                )
            ),
            ArbeidsuforhetRules(
                ArbeidsuforhetRulePayload(
                    sykmeldingId = sykmeldingId,
                    hoveddiagnose = null,
                    bidiagnoser = emptyList(),
                    annenFraversArsak = null,
                )
            ),
            PasientUnder13Rules(
                PasientUnder13RulePayload(
                    sykmeldingId = sykmeldingId,
                    perioder = emptyList(),
                    pasientFodselsdato = LocalDateTime.now().toLocalDate(),
                )
            ),
            PeriodeRules(
                PeriodeRulePayload(
                    sykmeldingId = sykmeldingId,
                    perioder = emptyList(),
                    signaturdato = LocalDateTime.now(),
                )
            ),
            TilbakedateringRules(
                TilbakedateringRulePayload(
                    sykmeldingId = sykmeldingId,
                    signaturdato = LocalDateTime.now(),
                    perioder = emptyList(),
                    startdato = null,
                    hoveddiagnoseSystem = null,
                    begrunnelseIkkeKontakt = null,
                    ettersendingAv = null,
                    dagerForArbeidsgiverperiodeCheck = emptyList(),
                    forlengelse = null,
                )
            ),
        )

    return runRules(ruleSequence)
}

// TODO: Only for dev
fun main() {
    val results = runSykmeldingRules("123")

    println("RESULTS: ${results.size}")

    results.forEach { println(it.first.printRulePath()) }
}
