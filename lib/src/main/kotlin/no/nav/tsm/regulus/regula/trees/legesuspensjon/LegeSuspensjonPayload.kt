package no.nav.tsm.regulus.regula.trees.legesuspensjon

import no.nav.tsm.regulus.regula.executor.BasePayload

data class LegeSuspensjonPayload(
    override val sykmeldingId: String,
    val behandlerSuspendert: Boolean,
) : BasePayload
