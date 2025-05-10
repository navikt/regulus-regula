package no.nav.tsm.regulus.regula.rules.trees.hpr

import java.time.LocalDateTime
import no.nav.tsm.regulus.regula.dsl.RuleOutput
import no.nav.tsm.regulus.regula.executor.TreeExecutor
import no.nav.tsm.regulus.regula.payload.SykmelderGodkjenning
import no.nav.tsm.regulus.regula.payload.SykmelderTilleggskompetanse
import no.nav.tsm.regulus.regula.rules.shared.getStartdatoFromTidligereSykmeldinger
import no.nav.tsm.regulus.regula.rules.trees.hpr.extras.HelsepersonellKategori
import no.nav.tsm.regulus.regula.utils.daysBetween
import no.nav.tsm.regulus.regula.utils.earliestFom
import no.nav.tsm.regulus.regula.utils.latestTom

internal class HprRules(hprRulePayload: HprRulePayload) :
    TreeExecutor<HprRule, HprRulePayload>(hprRuleTree, hprRulePayload) {
    override fun getRule(rule: HprRule) = getHprRule(rule)
}

private fun getHprRule(rules: HprRule): HprRuleFn =
    when (rules) {
        HprRule.SYKMELDER_FINNES_I_HPR -> Rules.sykmelderFinnesIHPR
        HprRule.SYKMELDER_GYLDIG_I_HPR -> Rules.sykmelderGyldigHPR
        HprRule.SYKMELDER_HAR_AUTORISASJON_I_HPR -> Rules.sykmelderHarAutorisasjon
        HprRule.SYKMELDER_ER_LEGE_I_HPR -> Rules.sykmelderErLege
        HprRule.SYKMELDER_ER_TANNLEGE_I_HPR -> Rules.sykmelderErTannlege
        HprRule.SYKMELDER_ER_MANUELLTERAPEUT_I_HPR -> Rules.sykmelderErManuellterapeut
        HprRule.SYKMELDER_ER_FT_MED_TILLEGSKOMPETANSE_I_HPR ->
            Rules.sykmelderErFTMedTilligskompetanseSykmelding
        HprRule.SYKMELDER_ER_KI_MED_TILLEGSKOMPETANSE_I_HPR ->
            Rules.sykmelderErKIMedTilligskompetanseSykmelding
        HprRule.SYKEFRAVAER_OVER_12_UKER -> Rules.sykefravarOver12Uker
    }

private typealias HprRuleFn = (payload: HprRulePayload) -> RuleOutput<HprRule>

private val Rules =
    object {
        val sykmelderFinnesIHPR: HprRuleFn = { payload ->
            val harGodkjenninger = payload.sykmelderGodkjenninger != null

            RuleOutput(
                rule = HprRule.SYKMELDER_FINNES_I_HPR,
                ruleInputs = mapOf("harGodkjenninger" to harGodkjenninger),
                ruleResult = harGodkjenninger,
            )
        }

        val sykmelderGyldigHPR: HprRuleFn = { payload ->
            val sykmelderGodkjenninger = payload.sykmelderGodkjenninger ?: emptyList()

            val aktivAutorisasjon =
                sykmelderGodkjenninger.any {
                    (it.autorisasjon?.aktiv != null && it.autorisasjon.aktiv)
                }

            RuleOutput(
                rule = HprRule.SYKMELDER_GYLDIG_I_HPR,
                ruleInputs = mapOf("sykmelderGodkjenninger" to sykmelderGodkjenninger),
                ruleResult = aktivAutorisasjon,
            )
        }

        val sykmelderHarAutorisasjon: HprRuleFn = { payload ->
            val sykmelderGodkjenninger = payload.sykmelderGodkjenninger ?: emptyList()

            val gyldigeGodkjenninger =
                sykmelderGodkjenninger.any {
                    (it.autorisasjon?.aktiv != null &&
                        it.autorisasjon.aktiv &&
                        it.autorisasjon.oid == 7704 &&
                        it.autorisasjon.verdi != null &&
                        it.autorisasjon.verdi in arrayOf("1", "17", "4", "2", "14", "18"))
                }

            RuleOutput(
                ruleInputs = mapOf("sykmelderGodkjenninger" to sykmelderGodkjenninger),
                rule = HprRule.SYKMELDER_HAR_AUTORISASJON_I_HPR,
                ruleResult = gyldigeGodkjenninger,
            )
        }

        val sykmelderErLege: HprRuleFn = { payload ->
            val sykmelderGodkjenninger = payload.sykmelderGodkjenninger ?: emptyList()

            val sykmelderErLege =
                sjekkSykmelder(sykmelderGodkjenninger, HelsepersonellKategori.LEGE)

            RuleOutput(
                ruleInputs = mapOf("sykmelderGodkjenninger" to sykmelderGodkjenninger),
                rule = HprRule.SYKMELDER_ER_LEGE_I_HPR,
                ruleResult = sykmelderErLege,
            )
        }

        val sykmelderErTannlege: HprRuleFn = { payload ->
            val sykmelderGodkjenninger = payload.sykmelderGodkjenninger ?: emptyList()

            val sykmelderErTannlege =
                sjekkSykmelder(sykmelderGodkjenninger, HelsepersonellKategori.TANNLEGE)

            RuleOutput(
                ruleInputs = mapOf("sykmelderGodkjenninger" to sykmelderGodkjenninger),
                rule = HprRule.SYKMELDER_ER_TANNLEGE_I_HPR,
                ruleResult = sykmelderErTannlege,
            )
        }

        val sykmelderErManuellterapeut: HprRuleFn = { payload ->
            val sykmelderGodkjenninger = payload.sykmelderGodkjenninger ?: emptyList()

            val sykmelderErManuellterapeut =
                sjekkSykmelder(sykmelderGodkjenninger, HelsepersonellKategori.MANUELLTERAPEUT)

            RuleOutput(
                ruleInputs = mapOf("sykmelderGodkjenninger" to sykmelderGodkjenninger),
                rule = HprRule.SYKMELDER_ER_MANUELLTERAPEUT_I_HPR,
                ruleResult = sykmelderErManuellterapeut,
            )
        }

        val sykmelderErFTMedTilligskompetanseSykmelding: HprRuleFn = { payload ->
            val sykmelderGodkjenninger = payload.sykmelderGodkjenninger ?: emptyList()
            val genereringsTidspunkt = payload.signaturdato

            val erFtMedTilleggskompetanse =
                erHelsepersonellKategoriMedTilleggskompetanse(
                    sykmelderGodkjenninger,
                    genereringsTidspunkt,
                    HelsepersonellKategori.FYSIOTERAPAEUT,
                )

            val result = erFtMedTilleggskompetanse

            RuleOutput(
                ruleInputs =
                    mapOf(
                        "sykmelderGodkjenninger" to sykmelderGodkjenninger,
                        "genereringsTidspunkt" to genereringsTidspunkt,
                    ),
                rule = HprRule.SYKMELDER_ER_FT_MED_TILLEGSKOMPETANSE_I_HPR,
                ruleResult = result,
            )
        }

        val sykmelderErKIMedTilligskompetanseSykmelding: HprRuleFn = { payload ->
            val sykmelderGodkjenninger = payload.sykmelderGodkjenninger ?: emptyList()
            val genereringsTidspunkt = payload.signaturdato

            val erKIMedTilleggskompetanse =
                erHelsepersonellKategoriMedTilleggskompetanse(
                    sykmelderGodkjenninger,
                    genereringsTidspunkt,
                    HelsepersonellKategori.KIROPRAKTOR,
                )

            val result = erKIMedTilleggskompetanse

            RuleOutput(
                ruleInputs = mapOf("sykmelderGodkjenninger" to sykmelderGodkjenninger),
                rule = HprRule.SYKMELDER_ER_KI_MED_TILLEGSKOMPETANSE_I_HPR,
                ruleResult = result,
            )
        }

        val sykefravarOver12Uker: HprRuleFn = { payload ->
            val forsteFomDato = payload.aktivitet.earliestFom()
            val sisteTomDato = payload.aktivitet.latestTom()
            val sykmelderGodkjenninger = payload.sykmelderGodkjenninger ?: emptyList()
            val startdato =
                getStartdatoFromTidligereSykmeldinger(forsteFomDato, payload.tidligereSykmeldinger)

            val over12Uker =
                daysBetween(forsteFomDato, sisteTomDato) > 84 ||
                    (daysBetween(startdato, sisteTomDato) > 84)

            RuleOutput(
                ruleInputs =
                    mapOf(
                        "fom" to forsteFomDato,
                        "tom" to sisteTomDato,
                        "startDatoSykefravær" to startdato,
                        "sykmelderGodkjenninger" to sykmelderGodkjenninger,
                    ),
                rule = HprRule.SYKEFRAVAER_OVER_12_UKER,
                ruleResult = over12Uker,
            )
        }
    }

private fun erHelsepersonellKategoriMedTilleggskompetanse(
    sykmelderGodkjenninger: List<SykmelderGodkjenning>,
    genereringsTidspunkt: LocalDateTime,
    helsepersonellkategori: HelsepersonellKategori,
) =
    sykmelderGodkjenninger.any { godkjenning ->
        godkjenning.helsepersonellkategori?.verdi == helsepersonellkategori.verdi &&
            godkjenning.tillegskompetanse?.any { tillegskompetanse ->
                tillegskompetanse.avsluttetStatus == null &&
                    tillegskompetanse.gyldigPeriode(genereringsTidspunkt) &&
                    tillegskompetanse.type?.aktiv == true &&
                    tillegskompetanse.type.oid == 7702 &&
                    tillegskompetanse.type.verdi == "1"
            } ?: false
    }

private fun sjekkSykmelder(
    sykmelderGodkjenninger: List<SykmelderGodkjenning>,
    helsepersonellkategori: HelsepersonellKategori,
) =
    sykmelderGodkjenninger.any {
        (it.helsepersonellkategori?.aktiv != null &&
            it.autorisasjon?.aktiv == true &&
            harAktivHelsepersonellAutorisasjonsSom(
                sykmelderGodkjenninger,
                helsepersonellkategori.verdi,
            ))
    }

private fun harAktivHelsepersonellAutorisasjonsSom(
    sykmelderGodkjenninger: List<SykmelderGodkjenning>,
    helsepersonerVerdi: String,
): Boolean =
    sykmelderGodkjenninger.any { godkjenning ->
        godkjenning.helsepersonellkategori?.aktiv != null &&
            godkjenning.autorisasjon?.aktiv == true &&
            godkjenning.helsepersonellkategori.verdi != null &&
            godkjenning.helsepersonellkategori.let { it.aktiv && it.verdi == helsepersonerVerdi }
    }

private fun SykmelderTilleggskompetanse.gyldigPeriode(
    genereringsTidspunkt: LocalDateTime
): Boolean {
    val fom = gyldig?.fra?.toLocalDate()
    val tom = gyldig?.til?.toLocalDate()
    val genDate = genereringsTidspunkt.toLocalDate()

    if (fom == null) {
        return false
    }

    return fom.minusDays(1).isBefore(genDate) && (tom == null || tom.plusDays(1).isAfter(genDate))
}
