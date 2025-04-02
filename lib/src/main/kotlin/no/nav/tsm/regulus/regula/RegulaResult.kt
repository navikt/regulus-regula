package no.nav.tsm.regulus.regula

enum class RegulaStatus {
    OK,
    MANUAL_PROCESSING,
    INVALID,
}

data class TreeResult(
    val rulePath: String,
    val outcome: RegulaOutcome?,
    val juridisk: RegulaJuridiskHenvisning?,
)

data class RegulaResult(
    val status: RegulaStatus,
    val outcome: RegulaOutcome?,
    val results: List<TreeResult>,
)

data class RegulaOutcome(
    val status: RegulaStatus,
    val rule: String,
    val messageForUser: String,
    val messageForSender: String,
)
