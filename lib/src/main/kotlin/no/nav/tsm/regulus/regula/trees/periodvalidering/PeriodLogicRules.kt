package no.nav.tsm.regulus.regula.trees.periodvalidering

import java.time.LocalDate
import java.time.temporal.ChronoUnit
import no.nav.tsm.regulus.regula.dsl.RuleOutput
import no.nav.tsm.regulus.regula.executor.TreeExecutor
import no.nav.tsm.regulus.regula.utils.daysBetween
import no.nav.tsm.regulus.regula.utils.workdaysBetween

class PeriodLogicRules(payload: PeriodLogicRulePayload) :
    TreeExecutor<PeriodLogicRule, PeriodLogicRulePayload>(periodLogicRuleTree, payload) {
    override fun getRule(
        rule: PeriodLogicRule
    ): (PeriodLogicRulePayload) -> RuleOutput<PeriodLogicRule> = getPeriodRule(rule)
}

typealias PeriodRuleFn = (payload: PeriodLogicRulePayload) -> RuleOutput<PeriodLogicRule>

fun getPeriodRule(rules: PeriodLogicRule): PeriodRuleFn {
    return when (rules) {
        PeriodLogicRule.PERIODER_MANGLER -> Rules.periodeMangler
        PeriodLogicRule.FRADATO_ETTER_TILDATO -> Rules.fraDatoEtterTilDato
        PeriodLogicRule.OVERLAPPENDE_PERIODER -> Rules.overlappendePerioder
        PeriodLogicRule.OPPHOLD_MELLOM_PERIODER -> Rules.oppholdMellomPerioder
        PeriodLogicRule.IKKE_DEFINERT_PERIODE -> Rules.ikkeDefinertPeriode
        PeriodLogicRule.BEHANDLINGSDATO_ETTER_MOTTATTDATO -> Rules.behandslingsDatoEtterMottatDato
        PeriodLogicRule.AVVENTENDE_SYKMELDING_KOMBINERT -> Rules.avventendeKombinert
        PeriodLogicRule.MANGLENDE_INNSPILL_TIL_ARBEIDSGIVER -> Rules.manglendeInnspillArbeidsgiver
        PeriodLogicRule.AVVENTENDE_SYKMELDING_OVER_16_DAGER -> Rules.avventendeOver16Dager
        PeriodLogicRule.FOR_MANGE_BEHANDLINGSDAGER_PER_UKE -> Rules.forMangeBehandlingsDagerPrUke
        PeriodLogicRule.GRADERT_SYKMELDING_OVER_99_PROSENT -> Rules.gradertOver99Prosent
        PeriodLogicRule.SYKMELDING_MED_BEHANDLINGSDAGER -> Rules.inneholderBehandlingsDager
    }
}

private val Rules =
    object {
        val periodeMangler: PeriodRuleFn = { payload ->
            val perioder = payload.perioder
            val periodeMangler = perioder.isEmpty()

            RuleOutput(
                ruleInputs = mapOf("perioder" to perioder),
                rule = PeriodLogicRule.PERIODER_MANGLER,
                ruleResult = periodeMangler,
            )
        }

        val fraDatoEtterTilDato: PeriodRuleFn = { payload ->
            val perioder = payload.perioder

            val fraDatoEtterTilDato = perioder.any { it.fom.isAfter(it.tom) }

            RuleOutput(
                ruleInputs = mapOf("perioder" to perioder),
                rule = PeriodLogicRule.FRADATO_ETTER_TILDATO,
                ruleResult = fraDatoEtterTilDato,
            )
        }

        val overlappendePerioder: PeriodRuleFn = { payload ->
            val perioder = payload.perioder

            val overlappendePerioder =
                perioder.any { periodA ->
                    perioder
                        .filter { periodB -> periodB != periodA }
                        .any { periodB -> periodA.fom in periodB || periodA.tom in periodB }
                }

            RuleOutput(
                ruleInputs = mapOf("perioder" to perioder),
                rule = PeriodLogicRule.OVERLAPPENDE_PERIODER,
                ruleResult = overlappendePerioder,
            )
        }

        val oppholdMellomPerioder: PeriodRuleFn = { payload ->
            val periodeRanges = payload.perioder.sortedBy { it.fom }.map { it.fom to it.tom }

            var oppholdMellomPerioder = false
            for (i in 1 until periodeRanges.size) {
                oppholdMellomPerioder =
                    workdaysBetween(periodeRanges[i - 1].second, periodeRanges[i].first) > 0
                if (oppholdMellomPerioder == true) {
                    break
                }
            }

            RuleOutput(
                ruleInputs = mapOf("perioder" to payload.perioder),
                rule = PeriodLogicRule.OPPHOLD_MELLOM_PERIODER,
                ruleResult = oppholdMellomPerioder,
            )
        }

        val ikkeDefinertPeriode: PeriodRuleFn = { payload ->
            val perioder = payload.perioder

            val ikkeDefinertPeriode =
                perioder.any {
                    it.aktivitetIkkeMulig == null &&
                        it.gradert == null &&
                        it.avventendeInnspillTilArbeidsgiver.isNullOrEmpty() &&
                        !it.reisetilskudd &&
                        (it.behandlingsdager == null || it.behandlingsdager == 0)
                }

            RuleOutput(
                ruleInputs = mapOf("perioder" to perioder),
                rule = PeriodLogicRule.IKKE_DEFINERT_PERIODE,
                ruleResult = ikkeDefinertPeriode,
            )
        }

        val behandslingsDatoEtterMottatDato: PeriodRuleFn = { payload ->
            val behandletTidspunkt = payload.behandletTidspunkt
            val receivedDate = payload.receivedDate

            val behandslingsDatoEtterMottatDato = behandletTidspunkt > receivedDate.plusDays(1)

            RuleOutput(
                ruleInputs =
                    mapOf("behandslingsDatoEtterMottatDato" to behandslingsDatoEtterMottatDato),
                rule = PeriodLogicRule.BEHANDLINGSDATO_ETTER_MOTTATTDATO,
                ruleResult = behandslingsDatoEtterMottatDato,
            )
        }

        val avventendeKombinert: PeriodRuleFn = { payload ->
            val perioder = payload.perioder

            val avventendeKombinert =
                perioder.count { it.avventendeInnspillTilArbeidsgiver != null } != 0 &&
                    perioder.size > 1

            RuleOutput(
                ruleInputs = mapOf("avventendeKombinert" to avventendeKombinert),
                rule = PeriodLogicRule.AVVENTENDE_SYKMELDING_KOMBINERT,
                ruleResult = avventendeKombinert,
            )
        }

        val manglendeInnspillArbeidsgiver: PeriodRuleFn = { payload ->
            val perioder = payload.perioder

            val manglendeInnspillArbeidsgiver =
                perioder.any {
                    it.avventendeInnspillTilArbeidsgiver != null &&
                        it.avventendeInnspillTilArbeidsgiver?.trim().isNullOrEmpty()
                }

            RuleOutput(
                ruleInputs =
                    mapOf("manglendeInnspillArbeidsgiver" to manglendeInnspillArbeidsgiver),
                rule = PeriodLogicRule.MANGLENDE_INNSPILL_TIL_ARBEIDSGIVER,
                ruleResult = manglendeInnspillArbeidsgiver,
            )
        }

        val avventendeOver16Dager: PeriodRuleFn = { payload ->
            val perioder = payload.perioder

            val avventendeOver16Dager =
                perioder
                    .filter { it.avventendeInnspillTilArbeidsgiver != null }
                    .any { (daysBetween(it.fom, it.tom)) > 16 }

            RuleOutput(
                ruleInputs = mapOf("avventendeOver16Dager" to avventendeOver16Dager),
                rule = PeriodLogicRule.AVVENTENDE_SYKMELDING_OVER_16_DAGER,
                ruleResult = avventendeOver16Dager,
            )
        }

        val forMangeBehandlingsDagerPrUke: PeriodRuleFn = { payload ->
            val perioder = payload.perioder

            val forMangeBehandlingsDagerPrUke =
                perioder.any {
                    it.behandlingsdager != null && it.behandlingsdager!! > it.startedWeeksBetween()
                }

            RuleOutput(
                ruleInputs =
                    mapOf("forMangeBehandlingsDagerPrUke" to forMangeBehandlingsDagerPrUke),
                rule = PeriodLogicRule.FOR_MANGE_BEHANDLINGSDAGER_PER_UKE,
                ruleResult = forMangeBehandlingsDagerPrUke,
            )
        }

        val gradertOver99Prosent: PeriodRuleFn = { payload ->
            val perioder = payload.perioder

            val gradertOver99Prosent = perioder.mapNotNull { it.gradert }.any { it.grad > 99 }

            RuleOutput(
                ruleInputs = mapOf("gradertOver99Prosent" to gradertOver99Prosent),
                rule = PeriodLogicRule.GRADERT_SYKMELDING_OVER_99_PROSENT,
                ruleResult = gradertOver99Prosent,
            )
        }

        val inneholderBehandlingsDager: PeriodRuleFn = { payload ->
            val perioder = payload.perioder

            val inneholderBehandlingsDager = perioder.any { it.behandlingsdager != null }

            RuleOutput(
                ruleInputs = mapOf("inneholderBehandlingsDager" to inneholderBehandlingsDager),
                rule = PeriodLogicRule.SYKMELDING_MED_BEHANDLINGSDAGER,
                ruleResult = inneholderBehandlingsDager,
            )
        }
    }

fun ClosedRange<LocalDate>.startedWeeksBetween(): Int =
    ChronoUnit.WEEKS.between(start, endInclusive).toInt() + 1
