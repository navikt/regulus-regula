package no.nav.tsm.regulus.regula.rules.trees.periode

import java.time.LocalDateTime
import no.nav.tsm.regulus.regula.payload.Aktivitet

internal data class PeriodeRulePayload(
    val aktivitet: List<Aktivitet>,
    val behandletTidspunkt: LocalDateTime,
    val mottattDato: LocalDateTime,
)
