package no.nav.tsm.regulus.regula

import no.nav.tsm.regulus.regula.rules.trees.arbeidsuforhet.ArbeidsuforhetRulePayload
import no.nav.tsm.regulus.regula.rules.trees.dato.DatoRulePayload
import no.nav.tsm.regulus.regula.rules.trees.hpr.HprRulePayload
import no.nav.tsm.regulus.regula.rules.trees.legeSuspensjon.LegeSuspensjonRulePayload
import no.nav.tsm.regulus.regula.rules.trees.pasientUnder13.PasientUnder13RulePayload
import no.nav.tsm.regulus.regula.rules.trees.periode.PeriodeRulePayload
import no.nav.tsm.regulus.regula.rules.trees.tilbakedatering.TilbakedateringRulePayload
import no.nav.tsm.regulus.regula.rules.trees.validering.ValideringRulePayload

internal fun RegulaPayload.toLegeSuspensjonRulePayload(): LegeSuspensjonRulePayload {
    return LegeSuspensjonRulePayload(
        sykmeldingId = sykmeldingId,
        behandlerSuspendert = behandler.suspendert,
    )
}

internal fun RegulaPayload.toValideringRulePayload(): ValideringRulePayload {
    return ValideringRulePayload(
        sykmeldingId = sykmeldingId,
        perioder = perioder,
        utdypendeOpplysninger = utdypendeOpplysninger,
        rulesetVersion = if (meta is RegulaMeta.LegacyMeta) meta.rulesetVersion else "3",
        legekontorOrgnr = behandler.legekontorOrgnr,
        behandlerFnr = behandler.fnr,
        avsenderFnr = avsender.fnr,
        pasientIdent = pasient.ident,
    )
}

internal fun RegulaPayload.toPeriodeRulePayload(): PeriodeRulePayload {
    return PeriodeRulePayload(
        sykmeldingId = sykmeldingId,
        perioder = perioder,
        behandletTidspunkt = behandletTidspunkt,
        mottattDato =
            when (meta) {
                is RegulaMeta.LegacyMeta -> meta.mottattDato
                is RegulaMeta.Meta -> meta.sendtTidspunkt
            },
    )
}

internal fun RegulaPayload.toHprRulePayload(): HprRulePayload {
    return HprRulePayload(
        sykmeldingId = sykmeldingId,
        behandlerGodkjenninger = behandler.godkjenninger,
        perioder = perioder,
        signaturdato =
            when (meta) {
                is RegulaMeta.LegacyMeta -> meta.signaturdato
                is RegulaMeta.Meta -> meta.sendtTidspunkt
            },
        tidligereSykmeldinger = tidligereSykmeldinger,
    )
}

internal fun RegulaPayload.toArbeidsuforhetRulePayload(): ArbeidsuforhetRulePayload {
    return ArbeidsuforhetRulePayload(
        sykmeldingId = sykmeldingId,
        hoveddiagnose = hoveddiagnose,
        bidiagnoser = bidiagnoser ?: emptyList(),
        annenFravarsArsak = annenFravarsArsak,
    )
}

internal fun RegulaPayload.toPasientUnder13RulePayload(): PasientUnder13RulePayload {
    return PasientUnder13RulePayload(
        sykmeldingId = sykmeldingId,
        perioder = perioder,
        pasientFodselsdato = pasient.fodselsdato,
    )
}

internal fun RegulaPayload.toDatoRulePayload(): DatoRulePayload {
    return DatoRulePayload(
        sykmeldingId = sykmeldingId,
        perioder = perioder,
        signaturdato =
            when (meta) {
                is RegulaMeta.LegacyMeta -> meta.signaturdato
                is RegulaMeta.Meta -> meta.sendtTidspunkt
            },
    )
}

internal fun RegulaPayload.toTilbakedateringRulePayload(): TilbakedateringRulePayload {
    return TilbakedateringRulePayload(
        sykmeldingId = sykmeldingId,
        perioder = perioder,
        tidligereSykmeldinger = tidligereSykmeldinger,
        signaturdato =
            when (meta) {
                is RegulaMeta.LegacyMeta -> meta.signaturdato
                is RegulaMeta.Meta -> meta.sendtTidspunkt
            },
        hoveddiagnose = hoveddiagnose,
        begrunnelseIkkeKontakt = kontaktPasientBegrunnelseIkkeKontakt,
    )
}
