package no.nav.tsm.regulus.regula

enum class RegulaStatus {
    OK,
    MANUAL_PROCESSING,
    INVALID,
}

data class RegulaExecutedTree(
    val tree: RegulaTree,
    /** The tree's overall status */
    val status: RegulaStatus,
    /** Given that the tree resulted in a non-OK status, this will be the outcome details */
    val outcome: RegulaOutcome?,
    /**
     * The base juridisk for the tree, can be enhanced by the application to create the complete
     * juridisk vurdering to be produced to the PIK-topic.
     */
    val juridisk: RegulaJuridiskVurdering?,
)

sealed interface RegulaResult {
    val status: RegulaStatus
    val trees: List<RegulaExecutedTree>

    data class Ok(override val trees: List<RegulaExecutedTree>) : RegulaResult {
        override val status = RegulaStatus.OK
    }

    data class NotOk(
        override val status: RegulaStatus,
        override val trees: List<RegulaExecutedTree>,
        val outcome: RegulaOutcome,
    ) : RegulaResult

    val juridisk: List<RegulaJuridiskVurdering>
        get() = trees.mapNotNull { it.juridisk }
}

enum class RegulaOutcomeStatus {
    MANUAL_PROCESSING,
    INVALID,
}

data class RegulaOutcomeReason(val sykmeldt: String, val sykmelder: String)

data class RegulaOutcome(
    val status: RegulaOutcomeStatus,
    val tree: RegulaTree,
    val rule: String,
    val reason: RegulaOutcomeReason,
)
