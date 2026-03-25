package no.nav.tsm.regulus.regula.rules.trees.pasientUnder13

import java.time.LocalDate
import no.nav.tsm.regulus.regula.payload.Aktivitet

internal data class PasientUnder13RulePayload(
    val aktivitet: List<Aktivitet>,
    val pasientFodselsdato: LocalDate,
)
