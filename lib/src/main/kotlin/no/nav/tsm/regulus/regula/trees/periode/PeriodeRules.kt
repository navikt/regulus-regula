package no.nav.tsm.regulus.regula.trees.periode

import java.time.LocalDate
import java.time.temporal.ChronoUnit
import no.nav.tsm.regulus.regula.dsl.RuleOutput
import no.nav.tsm.regulus.regula.executor.TreeExecutor
import no.nav.tsm.regulus.regula.utils.daysBetween
import no.nav.tsm.regulus.regula.utils.workdaysBetween

internal class PeriodeRules(payload: PeriodeRulePayload) :
    TreeExecutor<PeriodeRule, PeriodeRulePayload>(periodeRuleTree, payload) {
    override fun getRule(rule: PeriodeRule): (PeriodeRulePayload) -> RuleOutput<PeriodeRule> =
        getPeriodeRule(rule)
}

private fun getPeriodeRule(rules: PeriodeRule): PeriodeRuleFn {
    return when (rules) {
        PeriodeRule.PERIODER_MANGLER -> Rules.periodeMangler
        PeriodeRule.FRADATO_ETTER_TILDATO -> Rules.fraDatoEtterTilDato
        PeriodeRule.OVERLAPPENDE_PERIODER -> Rules.overlappendePerioder
        PeriodeRule.OPPHOLD_MELLOM_PERIODER -> Rules.oppholdMellomPerioder
        PeriodeRule.IKKE_DEFINERT_PERIODE -> Rules.ikkeDefinertPeriode
        PeriodeRule.BEHANDLINGSDATO_ETTER_MOTTATTDATO -> Rules.behandslingsDatoEtterMottatDato
        PeriodeRule.AVVENTENDE_SYKMELDING_KOMBINERT -> Rules.avventendeKombinert
        PeriodeRule.MANGLENDE_INNSPILL_TIL_ARBEIDSGIVER -> Rules.manglendeInnspillArbeidsgiver
        PeriodeRule.AVVENTENDE_SYKMELDING_OVER_16_DAGER -> Rules.avventendeOver16Dager
        PeriodeRule.FOR_MANGE_BEHANDLINGSDAGER_PER_UKE -> Rules.forMangeBehandlingsDagerPrUke
        PeriodeRule.GRADERT_SYKMELDING_OVER_99_PROSENT -> Rules.gradertOver99Prosent
        PeriodeRule.SYKMELDING_MED_BEHANDLINGSDAGER -> Rules.inneholderBehandlingsDager
    }
}

private typealias PeriodeRuleFn = (payload: PeriodeRulePayload) -> RuleOutput<PeriodeRule>

private val Rules =
    object {
        val periodeMangler: PeriodeRuleFn = { payload ->
            val perioder = payload.perioder
            val periodeMangler = perioder.isEmpty()

            RuleOutput(
                ruleInputs = mapOf("perioder" to perioder),
                rule = PeriodeRule.PERIODER_MANGLER,
                ruleResult = periodeMangler,
            )
        }

        val fraDatoEtterTilDato: PeriodeRuleFn = { payload ->
            val perioder = payload.perioder

            val fraDatoEtterTilDato = perioder.any { it.fom.isAfter(it.tom) }

            RuleOutput(
                ruleInputs = mapOf("perioder" to perioder),
                rule = PeriodeRule.FRADATO_ETTER_TILDATO,
                ruleResult = fraDatoEtterTilDato,
            )
        }

        val overlappendePerioder: PeriodeRuleFn = { payload ->
            val perioder = payload.perioder

            val overlappendePerioder =
                perioder.any { periodA ->
                    perioder
                        .filter { periodB -> periodB != periodA }
                        .any { periodB -> periodA.fom in periodB || periodA.tom in periodB }
                }

            RuleOutput(
                ruleInputs = mapOf("perioder" to perioder),
                rule = PeriodeRule.OVERLAPPENDE_PERIODER,
                ruleResult = overlappendePerioder,
            )
        }

        val oppholdMellomPerioder: PeriodeRuleFn = { payload ->
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
                rule = PeriodeRule.OPPHOLD_MELLOM_PERIODER,
                ruleResult = oppholdMellomPerioder,
            )
        }

        val ikkeDefinertPeriode: PeriodeRuleFn = { payload ->
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
                rule = PeriodeRule.IKKE_DEFINERT_PERIODE,
                ruleResult = ikkeDefinertPeriode,
            )
        }

        val behandslingsDatoEtterMottatDato: PeriodeRuleFn = { payload ->
            val behandletTidspunkt = payload.behandletTidspunkt
            val receivedDate = payload.receivedDate

            val behandslingsDatoEtterMottatDato = behandletTidspunkt > receivedDate.plusDays(1)

            RuleOutput(
                ruleInputs =
                    mapOf("behandslingsDatoEtterMottatDato" to behandslingsDatoEtterMottatDato),
                rule = PeriodeRule.BEHANDLINGSDATO_ETTER_MOTTATTDATO,
                ruleResult = behandslingsDatoEtterMottatDato,
            )
        }

        val avventendeKombinert: PeriodeRuleFn = { payload ->
            val perioder = payload.perioder

            val avventendeKombinert =
                perioder.count { it.avventendeInnspillTilArbeidsgiver != null } != 0 &&
                    perioder.size > 1

            RuleOutput(
                ruleInputs = mapOf("avventendeKombinert" to avventendeKombinert),
                rule = PeriodeRule.AVVENTENDE_SYKMELDING_KOMBINERT,
                ruleResult = avventendeKombinert,
            )
        }

        val manglendeInnspillArbeidsgiver: PeriodeRuleFn = { payload ->
            val perioder = payload.perioder

            val manglendeInnspillArbeidsgiver =
                perioder.any {
                    it.avventendeInnspillTilArbeidsgiver != null &&
                        it.avventendeInnspillTilArbeidsgiver?.trim().isNullOrEmpty()
                }

            RuleOutput(
                ruleInputs =
                    mapOf("manglendeInnspillArbeidsgiver" to manglendeInnspillArbeidsgiver),
                rule = PeriodeRule.MANGLENDE_INNSPILL_TIL_ARBEIDSGIVER,
                ruleResult = manglendeInnspillArbeidsgiver,
            )
        }

        val avventendeOver16Dager: PeriodeRuleFn = { payload ->
            val perioder = payload.perioder

            val avventendeOver16Dager =
                perioder
                    .filter { it.avventendeInnspillTilArbeidsgiver != null }
                    .any { (daysBetween(it.fom, it.tom)) > 16 }

            RuleOutput(
                ruleInputs = mapOf("avventendeOver16Dager" to avventendeOver16Dager),
                rule = PeriodeRule.AVVENTENDE_SYKMELDING_OVER_16_DAGER,
                ruleResult = avventendeOver16Dager,
            )
        }

        val forMangeBehandlingsDagerPrUke: PeriodeRuleFn = { payload ->
            val perioder = payload.perioder

            val forMangeBehandlingsDagerPrUke =
                perioder.any {
                    it.behandlingsdager != null && it.behandlingsdager!! > it.startedWeeksBetween()
                }

            RuleOutput(
                ruleInputs =
                    mapOf("forMangeBehandlingsDagerPrUke" to forMangeBehandlingsDagerPrUke),
                rule = PeriodeRule.FOR_MANGE_BEHANDLINGSDAGER_PER_UKE,
                ruleResult = forMangeBehandlingsDagerPrUke,
            )
        }

        val gradertOver99Prosent: PeriodeRuleFn = { payload ->
            val perioder = payload.perioder

            val gradertOver99Prosent = perioder.mapNotNull { it.gradert }.any { it.grad > 99 }

            RuleOutput(
                ruleInputs = mapOf("gradertOver99Prosent" to gradertOver99Prosent),
                rule = PeriodeRule.GRADERT_SYKMELDING_OVER_99_PROSENT,
                ruleResult = gradertOver99Prosent,
            )
        }

        val inneholderBehandlingsDager: PeriodeRuleFn = { payload ->
            val perioder = payload.perioder

            val inneholderBehandlingsDager = perioder.any { it.behandlingsdager != null }

            RuleOutput(
                ruleInputs = mapOf("inneholderBehandlingsDager" to inneholderBehandlingsDager),
                rule = PeriodeRule.SYKMELDING_MED_BEHANDLINGSDAGER,
                ruleResult = inneholderBehandlingsDager,
            )
        }
    }

private fun ClosedRange<LocalDate>.startedWeeksBetween(): Int =
    ChronoUnit.WEEKS.between(start, endInclusive).toInt() + 1
