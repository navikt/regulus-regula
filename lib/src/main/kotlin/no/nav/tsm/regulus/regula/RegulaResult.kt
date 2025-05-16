package no.nav.tsm.regulus.regula

enum class RegulaStatus {
    OK,
    MANUAL_PROCESSING,
    INVALID,
}

data class TreeResult(
    val status: RegulaStatus,
    val rulePath: String,
    val ruleInputs: Map<String, Any>,
    val outcome: RegulaOutcome?,
    val juridisk: RegulaJuridiskHenvisning?,
)

sealed class RegulaResult(
    open val status: RegulaStatus,
    /** The entire result set from the executed chain. For internal library use only. */
    @Deprecated(message = "This is an internal API and should not be used outside of the library.")
    open val results: List<TreeResult>,
) {
    data class Ok(override val results: List<TreeResult>) :
        RegulaResult(status = RegulaStatus.OK, results)

    data class NotOk(
        override val status: RegulaStatus,
        val outcome: RegulaOutcome,
        override val results: List<TreeResult>,
    ) : RegulaResult(status, results)
}

data class RegulaOutcome(
    val status: RegulaStatus,
    val rule: String,
    val messageForUser: String,
    val messageForSender: String,
)
