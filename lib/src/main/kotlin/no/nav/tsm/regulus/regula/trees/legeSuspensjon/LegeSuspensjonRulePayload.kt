package no.nav.tsm.regulus.regula.trees.legeSuspensjon

import no.nav.tsm.regulus.regula.payload.BasePayload

data class LegeSuspensjonRulePayload(
    override val sykmeldingId: String,
    val behandlerSuspendert: Boolean,
) : BasePayload
