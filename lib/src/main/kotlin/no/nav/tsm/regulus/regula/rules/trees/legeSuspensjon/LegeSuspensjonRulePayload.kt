package no.nav.tsm.regulus.regula.rules.trees.legeSuspensjon

import no.nav.tsm.regulus.regula.executor.BasePayload

internal data class LegeSuspensjonRulePayload(
    override val sykmeldingId: String,
    val behandlerSuspendert: Boolean,
) : BasePayload
