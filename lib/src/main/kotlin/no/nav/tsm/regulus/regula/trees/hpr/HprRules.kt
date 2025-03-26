package no.nav.tsm.regulus.regula.trees.hpr

import java.time.LocalDateTime
import no.nav.tsm.regulus.regula.dsl.RuleOutput
import no.nav.tsm.regulus.regula.executor.TreeExecutor
import no.nav.tsm.regulus.regula.trees.hpr.extras.HelsepersonellKategori
import no.nav.tsm.regulus.regula.utils.daysBetween
import no.nav.tsm.regulus.regula.utils.earliestFom
import no.nav.tsm.regulus.regula.utils.latestTom

internal class HprRules(hprRulePayload: HprRulePayload) :
    TreeExecutor<HprRule, HprRulePayload>(hprRuleTree, hprRulePayload) {
    override fun getRule(rule: HprRule) = getHprRule(rule)
}

private fun getHprRule(rules: HprRule): HprRuleFn =
    when (rules) {
        HprRule.BEHANDLER_GYLIDG_I_HPR -> Rules.behanderGyldigHPR
        HprRule.BEHANDLER_HAR_AUTORISASJON_I_HPR -> Rules.behandlerHarAutorisasjon
        HprRule.BEHANDLER_ER_LEGE_I_HPR -> Rules.behandlerErLege
        HprRule.BEHANDLER_ER_TANNLEGE_I_HPR -> Rules.behandlerErTannlege
        HprRule.BEHANDLER_ER_MANUELLTERAPEUT_I_HPR -> Rules.behandlerErManuellterapeut
        HprRule.BEHANDLER_ER_FT_MED_TILLEGSKOMPETANSE_I_HPR ->
            Rules.behandlerErFTMedTilligskompetanseSykmelding
        HprRule.BEHANDLER_ER_KI_MED_TILLEGSKOMPETANSE_I_HPR ->
            Rules.behandlerErKIMedTilligskompetanseSykmelding
        HprRule.SYKEFRAVAR_OVER_12_UKER -> Rules.sykefravarOver12Uker
    }

private typealias HprRuleFn = (payload: HprRulePayload) -> RuleOutput<HprRule>

private val Rules =
    object {
        val behanderGyldigHPR: HprRuleFn = { payload ->
            val behandlerGodkjenninger = payload.behandler.godkjenninger

            val aktivAutorisasjon =
                behandlerGodkjenninger.any {
                    (it.autorisasjon?.aktiv != null && it.autorisasjon.aktiv)
                }

            RuleOutput(
                rule = HprRule.BEHANDLER_GYLIDG_I_HPR,
                ruleInputs = mapOf("behandlerGodkjenninger" to behandlerGodkjenninger),
                ruleResult = aktivAutorisasjon,
            )
        }

        val behandlerHarAutorisasjon: HprRuleFn = { payload ->
            val behandlerGodkjenninger = payload.behandler.godkjenninger

            val gyldigeGodkjenninger =
                behandlerGodkjenninger.any {
                    (it.autorisasjon?.aktiv != null &&
                        it.autorisasjon.aktiv &&
                        it.autorisasjon.oid == 7704 &&
                        it.autorisasjon.verdi != null &&
                        it.autorisasjon.verdi in arrayOf("1", "17", "4", "2", "14", "18"))
                }

            RuleOutput(
                ruleInputs = mapOf("behandlerGodkjenninger" to behandlerGodkjenninger),
                rule = HprRule.BEHANDLER_HAR_AUTORISASJON_I_HPR,
                ruleResult = gyldigeGodkjenninger,
            )
        }

        val behandlerErLege: HprRuleFn = { payload ->
            val behandlerGodkjenninger = payload.behandler.godkjenninger

            val behandlerErLege =
                sjekkBehandler(behandlerGodkjenninger, HelsepersonellKategori.LEGE)

            RuleOutput(
                ruleInputs = mapOf("behandlerGodkjenninger" to behandlerGodkjenninger),
                rule = HprRule.BEHANDLER_ER_LEGE_I_HPR,
                ruleResult = behandlerErLege,
            )
        }

        val behandlerErTannlege: HprRuleFn = { payload ->
            val behandlerGodkjenninger = payload.behandler.godkjenninger

            val behandlerErTannlege =
                sjekkBehandler(behandlerGodkjenninger, HelsepersonellKategori.TANNLEGE)

            RuleOutput(
                ruleInputs = mapOf("behandlerGodkjenninger" to behandlerGodkjenninger),
                rule = HprRule.BEHANDLER_ER_TANNLEGE_I_HPR,
                ruleResult = behandlerErTannlege,
            )
        }

        val behandlerErManuellterapeut: HprRuleFn = { payload ->
            val behandlerGodkjenninger = payload.behandler.godkjenninger

            val behandlerErManuellterapeut =
                sjekkBehandler(behandlerGodkjenninger, HelsepersonellKategori.MANUELLTERAPEUT)

            RuleOutput(
                ruleInputs = mapOf("behandlerGodkjenninger" to behandlerGodkjenninger),
                rule = HprRule.BEHANDLER_ER_MANUELLTERAPEUT_I_HPR,
                ruleResult = behandlerErManuellterapeut,
            )
        }

        val behandlerErFTMedTilligskompetanseSykmelding: HprRuleFn = { payload ->
            val behandlerGodkjenninger = payload.behandler.godkjenninger
            val genereringsTidspunkt = payload.signaturdato

            val erFtMedTilleggskompetanse =
                erHelsepersonellKategoriMedTilleggskompetanse(
                    behandlerGodkjenninger,
                    genereringsTidspunkt,
                    HelsepersonellKategori.FYSIOTERAPAEUT,
                )

            val result = erFtMedTilleggskompetanse

            RuleOutput(
                ruleInputs =
                    mapOf(
                        "behandlerGodkjenninger" to behandlerGodkjenninger,
                        "genereringsTidspunkt" to genereringsTidspunkt,
                    ),
                rule = HprRule.BEHANDLER_ER_FT_MED_TILLEGSKOMPETANSE_I_HPR,
                ruleResult = result,
            )
        }

        val behandlerErKIMedTilligskompetanseSykmelding: HprRuleFn = { payload ->
            val behandlerGodkjenninger = payload.behandler.godkjenninger
            val genereringsTidspunkt = payload.signaturdato

            val erKIMedTilleggskompetanse =
                erHelsepersonellKategoriMedTilleggskompetanse(
                    behandlerGodkjenninger,
                    genereringsTidspunkt,
                    HelsepersonellKategori.KIROPRAKTOR,
                )

            val result = erKIMedTilleggskompetanse

            RuleOutput(
                ruleInputs = mapOf("behandlerGodkjenninger" to behandlerGodkjenninger),
                rule = HprRule.BEHANDLER_ER_KI_MED_TILLEGSKOMPETANSE_I_HPR,
                ruleResult = result,
            )
        }

        val sykefravarOver12Uker: HprRuleFn = { payload ->
            val forsteFomDato = payload.perioder.earliestFom()
            val sisteTomDato = payload.perioder.latestTom()
            val behandlerStartDato = payload.startdato
            val behandlerGodkjenninger = payload.behandler.godkjenninger

            val over12Uker =
                daysBetween(forsteFomDato, sisteTomDato) > 84 ||
                    (behandlerStartDato != null &&
                        daysBetween(behandlerStartDato, sisteTomDato) > 84)

            RuleOutput(
                ruleInputs =
                    mapOf(
                        "fom" to forsteFomDato,
                        "tom" to sisteTomDato,
                        "startDatoSykefravær" to (behandlerStartDato ?: forsteFomDato),
                        "behandlerGodkjenninger" to behandlerGodkjenninger,
                    ),
                rule = HprRule.SYKEFRAVAR_OVER_12_UKER,
                ruleResult = over12Uker,
            )
        }
    }

private fun erHelsepersonellKategoriMedTilleggskompetanse(
    behandlerGodkjenninger: List<Godkjenning>,
    genereringsTidspunkt: LocalDateTime,
    helsepersonellkategori: HelsepersonellKategori,
) =
    behandlerGodkjenninger.any { godkjenning ->
        godkjenning.helsepersonellkategori?.verdi == helsepersonellkategori.verdi &&
            godkjenning.tillegskompetanse?.any { tillegskompetanse ->
                tillegskompetanse.avsluttetStatus == null &&
                    tillegskompetanse.gyldigPeriode(genereringsTidspunkt) &&
                    tillegskompetanse.type?.aktiv == true &&
                    tillegskompetanse.type.oid == 7702 &&
                    tillegskompetanse.type.verdi == "1"
            } ?: false
    }

private fun sjekkBehandler(
    behandlerGodkjenninger: List<Godkjenning>,
    helsepersonellkategori: HelsepersonellKategori,
) =
    behandlerGodkjenninger.any {
        (it.helsepersonellkategori?.aktiv != null &&
            it.autorisasjon?.aktiv == true &&
            harAktivHelsepersonellAutorisasjonsSom(
                behandlerGodkjenninger,
                helsepersonellkategori.verdi,
            ))
    }

private fun harAktivHelsepersonellAutorisasjonsSom(
    behandlerGodkjenninger: List<Godkjenning>,
    helsepersonerVerdi: String,
): Boolean =
    behandlerGodkjenninger.any { godkjenning ->
        godkjenning.helsepersonellkategori?.aktiv != null &&
            godkjenning.autorisasjon?.aktiv == true &&
            godkjenning.helsepersonellkategori.verdi != null &&
            godkjenning.helsepersonellkategori.let { it.aktiv && it.verdi == helsepersonerVerdi }
    }

private fun Tilleggskompetanse.gyldigPeriode(genereringsTidspunkt: LocalDateTime): Boolean {
    val fom = gyldig?.fra?.toLocalDate()
    val tom = gyldig?.til?.toLocalDate()
    val genDate = genereringsTidspunkt.toLocalDate()

    if (fom == null) {
        return false
    }

    return fom.minusDays(1).isBefore(genDate) && (tom == null || tom.plusDays(1).isAfter(genDate))
}
