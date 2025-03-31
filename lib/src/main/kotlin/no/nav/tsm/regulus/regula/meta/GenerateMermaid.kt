package no.nav.tsm.regulus.regula.meta

import no.nav.tsm.regulus.regula.dsl.*
import no.nav.tsm.regulus.regula.dsl.RuleStatus
import no.nav.tsm.regulus.regula.dsl.TreeNode.*
import no.nav.tsm.regulus.regula.rules.trees.arbeidsuforhet.arbeidsuforhetRuleTree
import no.nav.tsm.regulus.regula.rules.trees.dato.datoRuleTree
import no.nav.tsm.regulus.regula.rules.trees.hpr.hprRuleTree
import no.nav.tsm.regulus.regula.rules.trees.legeSuspensjon.legeSuspensjonRuleTree
import no.nav.tsm.regulus.regula.rules.trees.pasientUnder13.pasientUnder13RuleTree
import no.nav.tsm.regulus.regula.rules.trees.periode.periodeRuleTree
import no.nav.tsm.regulus.regula.rules.trees.tilbakedatering.tilbakedateringRuleTree
import no.nav.tsm.regulus.regula.rules.trees.validering.valideringRuleTree

fun main() {
    val ruleTrees =
        listOf(
            "Suspendert lege" to legeSuspensjonRuleTree,
            "Strukturell validering" to valideringRuleTree,
            "Sykmeldingsperioder" to periodeRuleTree,
            "Behandler i HPR" to hprRuleTree,
            "Arbeidsuførhet" to arbeidsuforhetRuleTree,
            "Pasient under 13" to pasientUnder13RuleTree,
            "Dato" to datoRuleTree,
            "Tilbakedatering" to tilbakedateringRuleTree,
        )

    ruleTrees.forEachIndexed { idx, (name, ruleTree) -> // add index to differentiate each loop
        val builder = StringBuilder()
        builder.append("## $idx. $name\n\n") // section headers with added index number

        builder.append("```mermaid\n")
        builder.append("graph TD\n")
        ruleTree.traverseTree(builder, "root", "root")
        builder.append("    classDef ok fill:#c3ff91,stroke:#004a00,color: black;\n")
        builder.append("    classDef invalid fill:#ff7373,stroke:#ff0000,color: black;\n")
        builder.append("    classDef manuell fill:#ffe24f,stroke:#ffd500,color: #473c00;\n")
        builder.append("```\n\n")

        println(builder.toString())
    }
}

private fun <Enum> TreeNode<Enum>.traverseTree(
    builder: StringBuilder,
    thisNodeKey: String,
    nodeKey: String,
) {
    when (this) {
        is LeafNode -> {
            // Is handled by parent node
            return
        }

        is RuleNode -> {
            val currentNodeKey = "${nodeKey}_$rule"
            if (yes is LeafNode) {
                val childResult = (yes as LeafNode).status
                val childKey = "${currentNodeKey}_$childResult"

                if (childResult == RuleStatus.INVALID) {
                    builder.append(
                        "    $thisNodeKey($rule) -->|Ja| ${childKey}(${childResult.norsk()})${
                            getStyle(
                                childResult
                            )
                        }\n"
                    )
                    builder.append(
                        "    $thisNodeKey($rule) -->|\"Ja (papir)\"| ${childKey}_papir(${RuleStatus.MANUAL_PROCESSING.norsk()})${getStyle(
                            RuleStatus.MANUAL_PROCESSING)}\n"
                    )
                } else {
                    builder.append(
                        "    $thisNodeKey($rule) -->|Ja| $childKey(${childResult.norsk()})${
                            getStyle(
                                childResult
                            )
                        }\n"
                    )
                }
            } else {
                val childRule = (yes as RuleNode<Enum>).rule
                val childKey = "${currentNodeKey}_$childRule"
                builder.append("    $thisNodeKey($rule) -->|Ja| $childKey($childRule)\n")
                yes.traverseTree(builder, childKey, currentNodeKey)
            }
            if (no is LeafNode) {
                val childResult = (no as LeafNode).status
                val childKey = "${currentNodeKey}_$childResult"
                if (childResult == RuleStatus.INVALID) {
                    builder.append(
                        "    $thisNodeKey($rule) -->|Nei| $childKey(${childResult.norsk()})${
                            getStyle(
                                childResult
                            )
                        }\n"
                    )
                    builder.append(
                        "    $thisNodeKey($rule) -->|\"Nei (papir)\"| ${childKey}_papir(${RuleStatus.MANUAL_PROCESSING.norsk()})${
                            getStyle(
                                RuleStatus.MANUAL_PROCESSING
                            )
                        }\n"
                    )
                } else {
                    builder.append(
                        "    $thisNodeKey($rule) -->|Nei| $childKey(${childResult.norsk()})${
                            getStyle(
                                childResult
                            )
                        }\n"
                    )
                }
            } else {
                val childRule = (no as RuleNode<Enum>).rule
                val childKey = "${currentNodeKey}_$childRule"
                builder.append("    $thisNodeKey($rule) -->|Nei| $childKey($childRule)\n")
                no.traverseTree(builder, "${currentNodeKey}_$childRule", currentNodeKey)
            }
        }
    }
}

private fun RuleStatus.norsk(): String =
    when (this) {
        RuleStatus.OK -> "OK"
        RuleStatus.INVALID -> "Ugyldig"
        RuleStatus.MANUAL_PROCESSING -> "Manuell behandling"
    }

private fun getStyle(childResult: RuleStatus): String =
    when (childResult) {
        RuleStatus.OK -> ":::ok"
        RuleStatus.INVALID -> ":::invalid"
        RuleStatus.MANUAL_PROCESSING -> ":::manuell"
    }
