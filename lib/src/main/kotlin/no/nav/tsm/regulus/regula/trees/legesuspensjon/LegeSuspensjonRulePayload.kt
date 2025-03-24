package no.nav.tsm.regulus.regula.trees.legesuspensjon

import no.nav.tsm.regulus.regula.payload.BasePayload

data class LegeSuspensjonRulePayload(
    override val sykmeldingId: String,
    val behandlerSuspendert: Boolean,
) : BasePayload
