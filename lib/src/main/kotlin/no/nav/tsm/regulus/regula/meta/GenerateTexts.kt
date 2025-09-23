package no.nav.tsm.regulus.regula.meta

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import no.nav.tsm.regulus.regula.rules.trees.arbeidsuforhet.ArbeidsuforhetRule
import no.nav.tsm.regulus.regula.rules.trees.dato.DatoRule
import no.nav.tsm.regulus.regula.rules.trees.hpr.HprRule
import no.nav.tsm.regulus.regula.rules.trees.legeSuspensjon.LegeSuspensjonRule
import no.nav.tsm.regulus.regula.rules.trees.pasientUnder13.PasientUnder13Rule
import no.nav.tsm.regulus.regula.rules.trees.periode.PeriodeRule
import no.nav.tsm.regulus.regula.rules.trees.tilbakedatering.TilbakedateringRule
import no.nav.tsm.regulus.regula.rules.trees.validering.ValideringRule

fun main() {
    val ruleTrees =
        listOf(
            "Suspendert lege" to LegeSuspensjonRule.Outcomes.entries.toTypedArray(),
            "Strukturell validering" to ValideringRule.Outcomes.entries.toTypedArray(),
            "Sykmeldingsperioder" to PeriodeRule.Outcomes.entries.toTypedArray(),
            "Behandler i HPR" to HprRule.Outcomes.entries.toTypedArray(),
            "Arbeidsuførhet" to ArbeidsuforhetRule.Outcomes.entries.toTypedArray(),
            "Pasient under 13" to PasientUnder13Rule.Outcomes.entries.toTypedArray(),
            "Dato" to DatoRule.Outcomes.entries.toTypedArray(),
            "Tilbakedatering" to TilbakedateringRule.Outcomes.entries.toTypedArray(),
        )

    val rules = buildJsonObject {
        ruleTrees.forEachIndexed { idx, (name, rules) -> // add index to differentiate each loop
            put(
                name,
                buildJsonObject { rules.forEach { rule -> put(rule.name, rule.messageForSender) } },
            )
        }
    }

    val pretty =
        Json {
                prettyPrint = true
                prettyPrintIndent = "  "
            }
            .encodeToString(JsonObject.serializer(), rules)

    println(pretty)
}
