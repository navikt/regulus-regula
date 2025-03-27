package no.nav.tsm.regulus.regula

enum class RegulaStatus {
    OK,
    MANUAL_PROCESSING,
    INVALID,
}

data class TreeResult(val outcome: RegulaOutcome?, val rulePath: String)

data class RegulaResult(
    val status: RegulaStatus,
    // TODO: Can this ever be anything else than one?
    val ruleHits: List<RegulaOutcome>,
    val results: List<TreeResult>,
)

data class RegulaOutcome(
    val status: RegulaStatus,
    val rule: String,
    val messageForUser: String,
    val messageForSender: String,
)
