package no.nav.tsm.regulus.regula

import no.nav.tsm.regulus.regula.executor.ExecutionMode
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
        behandlerSuspendert =
            if (behandler is RegulaBehandler.Finnes) behandler.suspendert else false,
    )
}

internal fun RegulaPayload.toValideringRulePayload(mode: ExecutionMode): ValideringRulePayload {
    return ValideringRulePayload(
        sykmeldingId = sykmeldingId,
        aktivitet = aktivitet,
        utdypendeOpplysninger = utdypendeOpplysninger,
        rulesetVersion = if (meta is RegulaMeta.LegacyMeta) meta.rulesetVersion else "3",
        papirsykmelding = mode == ExecutionMode.PAPIR,
        legekontorOrgnr =
            if (behandler is RegulaBehandler.Finnes) behandler.legekontorOrgnr else null,
        behandlerFnr = behandler.fnr,
        avsenderFnr = if (avsender is RegulaAvsender.Finnes) avsender.fnr else null,
        pasientIdent = pasient.ident,
        tidligereSykmeldinger = tidligereSykmeldinger,
    )
}

internal fun RegulaPayload.toPeriodeRulePayload(): PeriodeRulePayload {
    return PeriodeRulePayload(
        sykmeldingId = sykmeldingId,
        aktivitet = aktivitet,
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
        behandlerGodkjenninger =
            if (behandler is RegulaBehandler.Finnes) behandler.godkjenninger else null,
        aktivitet = aktivitet,
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
        aktivitet = aktivitet,
        pasientFodselsdato = pasient.fodselsdato,
    )
}

internal fun RegulaPayload.toDatoRulePayload(): DatoRulePayload {
    return DatoRulePayload(
        sykmeldingId = sykmeldingId,
        aktivitet = aktivitet,
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
        aktivitet = aktivitet,
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
