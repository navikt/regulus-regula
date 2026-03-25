package no.nav.tsm.regulus.regula.rules.trees.dato

import java.time.LocalDateTime
import no.nav.tsm.regulus.regula.payload.Aktivitet

internal data class DatoRulePayload(
    val aktivitet: List<Aktivitet>,
    val signaturdato: LocalDateTime,
)
