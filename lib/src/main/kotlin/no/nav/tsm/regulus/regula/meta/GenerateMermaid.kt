package no.nav.tsm.regulus.regula.meta

import no.nav.tsm.regulus.regula.RegulaJuridiskHenvisning
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

private val renderJuridiskHenvisning = System.getenv("JURIDISK_HENVISNING")?.toBoolean() ?: false

fun main() {
    val ruleTrees =
        listOf(
            "Suspendert lege" to legeSuspensjonRuleTree,
            "Strukturell validering" to valideringRuleTree,
            "Sykmeldingsperioder" to periodeRuleTree,
            "Sykmelder i HPR" to hprRuleTree,
            "Arbeidsuførhet" to arbeidsuforhetRuleTree,
            "Pasient under 13" to pasientUnder13RuleTree,
            "Dato" to datoRuleTree,
            "Tilbakedatering" to tilbakedateringRuleTree,
        )

    ruleTrees.forEachIndexed { idx, (name, ruleTree) -> // add index to differentiate each loop
        val builder = StringBuilder()
        builder.append("## $idx. $name\n\n") // section headers with added index number

        val juridiskeHenvisninger: List<RegulaJuridiskHenvisning> =
            ruleTree.extractJuridiskHenvisninger().distinctBy { it.paragraf }

        require(juridiskeHenvisninger.size <= 1) {
            "There are two different paragraphs used in $name tree!\n " +
                "Please check the rule tree and make sure that only one paragraph is used.\n" +
                "Found: ${juridiskeHenvisninger.joinToString { it.paragraf }}"
        }

        if (juridiskeHenvisninger.size == 1) {
            val henvisning = juridiskeHenvisninger.first()

            builder.append("---\n\n")
            builder.append("- ### Juridisk Henvisning:\n") // sub-section header
            henvisning.lovverk.let { builder.append("  - **Lovverk**: $it\n") }
            henvisning.paragraf.let { builder.append("  - **Paragraf**: $it\n") }

            builder.append("\n---\n\n")
        }

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

private fun <Enum> TreeNode<Enum>.extractJuridiskHenvisninger(): List<RegulaJuridiskHenvisning> {
    return when (this) {
        is LeafNode -> {
            if (juridisk.juridiskHenvisning != null) {
                listOf(juridisk.juridiskHenvisning)
            } else {
                emptyList()
            }
        }

        is RuleNode -> {
            val yesJuridisk = yes.extractJuridiskHenvisninger()
            val noJuridisk = no.extractJuridiskHenvisninger()
            yesJuridisk + noJuridisk
        }
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
            val yesNode = yes

            if (yesNode is LeafNode) {
                val childResult = yesNode.status
                val childKey = "${currentNodeKey}_$childResult"

                if (childResult == RuleStatus.INVALID) {
                    builder.append(
                        "    $thisNodeKey($rule) -->|Ja| ${childKey}(${childResult.norsk()}${yesNode.juridisk.folkelig()})${
                            getStyle(
                                childResult
                            )
                        }\n"
                    )
                    if (yesNode.juridisk.juridiskHenvisning != null && renderJuridiskHenvisning) {
                        val henvisning = yesNode.juridisk.juridiskHenvisning
                        builder.append(
                            "    click ${childKey} \"${henvisning.hyperlenke()}\" \"Gå til lovdata\"\n"
                        )
                    }
                    builder.append(
                        "    $thisNodeKey($rule) -->|\"Ja (papir)\"| ${childKey}_papir(${RuleStatus.MANUAL_PROCESSING.norsk()}${yesNode.juridisk.folkelig()})${getStyle(
                            RuleStatus.MANUAL_PROCESSING)}\n"
                    )
                    if (yesNode.juridisk.juridiskHenvisning != null && renderJuridiskHenvisning) {
                        val henvisning = yesNode.juridisk.juridiskHenvisning
                        builder.append(
                            "    click ${childKey}_papir \"${henvisning.hyperlenke()}\" \"Gå til lovdata\"\n"
                        )
                    }
                } else {
                    builder.append(
                        "    $thisNodeKey($rule) -->|Ja| $childKey(${childResult.norsk()}${yesNode.juridisk.folkelig()})${
                            getStyle(
                                childResult
                            )
                        }\n"
                    )
                    if (yesNode.juridisk.juridiskHenvisning != null && renderJuridiskHenvisning) {
                        val henvisning = yesNode.juridisk.juridiskHenvisning
                        builder.append(
                            "    click ${childKey} \"${henvisning.hyperlenke()}\" \"Gå til lovdata\"\n"
                        )
                    }
                }
            } else {
                val childRule = (yesNode as RuleNode<Enum>).rule
                val childKey = "${currentNodeKey}_$childRule"
                builder.append("    $thisNodeKey($rule) -->|Ja| $childKey($childRule)\n")
                yes.traverseTree(builder, childKey, currentNodeKey)
            }

            val noNode = no
            if (noNode is LeafNode) {
                val childResult = noNode.status
                val childKey = "${currentNodeKey}_$childResult"
                if (childResult == RuleStatus.INVALID) {
                    builder.append(
                        "    $thisNodeKey($rule) -->|Nei| $childKey(${childResult.norsk()}${noNode.juridisk.folkelig()})${
                            getStyle(
                                childResult
                            )
                        }\n"
                    )
                    builder.append(
                        "    $thisNodeKey($rule) -->|\"Nei (papir)\"| ${childKey}_papir(${RuleStatus.MANUAL_PROCESSING.norsk()}${noNode.juridisk.folkelig()})${
                            getStyle(
                                RuleStatus.MANUAL_PROCESSING
                            )
                        }\n"
                    )
                } else {
                    builder.append(
                        "    $thisNodeKey($rule) -->|Nei| $childKey(${childResult.norsk()}${noNode.juridisk.folkelig()})${
                            getStyle(
                                childResult
                            )
                        }\n"
                    )
                    if (noNode.juridisk.juridiskHenvisning != null && renderJuridiskHenvisning) {
                        val henvisning = noNode.juridisk.juridiskHenvisning
                        builder.append(
                            "    click ${childKey} \"${henvisning.hyperlenke()}\" \"Gå til lovdata\"\n"
                        )
                    }
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

private fun RegulaJuridiskHenvisning.hyperlenke(): String =
    "https://lovdata.no/nav/folketrygdloven/kap${this.paragraf.split("-").first()}#PARAGRAF_${this.paragraf}"

private fun RuleJuridisk.folkelig(): String {
    if (!renderJuridiskHenvisning) return ""

    return when (this.juridiskHenvisning) {
        null -> ""
        else -> {
            val it = this.juridiskHenvisning
            buildString {
                append("\n")
                append(it.lovverk.kortnavn)
                append("\n")
                append(" § ${it.paragraf}")
                it.ledd?.let { ledd -> append("-$ledd") }
                it.punktum?.let { pkt -> append(" ${pkt}.") }
                it.bokstav?.let { bokstav -> append(" ${bokstav.lowercase()})") }
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
