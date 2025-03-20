package no.nav.tsm.regulus.regula.trees.legesuspensjon

import no.nav.tsm.regulus.regula.payload.BasePayload

data class LegeSuspensjonPayload(
    override val sykmeldingId: String,
    val behandlerSuspendert: Boolean,
) : BasePayload
