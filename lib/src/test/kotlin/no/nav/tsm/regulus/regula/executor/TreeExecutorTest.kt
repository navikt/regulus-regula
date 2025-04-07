package no.nav.tsm.regulus.regula.executor

import kotlin.test.Test
import no.nav.tsm.regulus.regula.dsl.RuleJuridisk
import no.nav.tsm.regulus.regula.dsl.RuleOutcome
import no.nav.tsm.regulus.regula.dsl.RuleOutput
import no.nav.tsm.regulus.regula.dsl.RuleStatus
import no.nav.tsm.regulus.regula.dsl.TreeNode.LeafNode.*
import no.nav.tsm.regulus.regula.dsl.getOutcome
import no.nav.tsm.regulus.regula.dsl.tree
import no.nav.tsm.regulus.regula.rules.trees.assertPath
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Nested

private enum class TestEnumRule {
    RULE_1,
    RULE_2,
    RULE_3;

    enum class Outcomes(
        override val status: RuleStatus,
        override val messageForSender: String,
        override val messageForUser: String,
    ) : RuleOutcome {
        ITS_NOT_LOOKING_GOOD(
            status = RuleStatus.INVALID,
            messageForSender = "Sender",
            messageForUser = "User",
        ),
        CERTAIN_DEATH(status = RuleStatus.OK, messageForSender = "Sender", messageForUser = "User"),
    }
}

private val testTree =
    tree(TestEnumRule.RULE_1) {
        yes(TestEnumRule.RULE_2) {
            yes(TestEnumRule.RULE_3) {
                yes(OK(RuleJuridisk.INGEN))
                no(INVALID(TestEnumRule.Outcomes.CERTAIN_DEATH, RuleJuridisk.INGEN))
            }
            no(MANUAL(TestEnumRule.Outcomes.ITS_NOT_LOOKING_GOOD, RuleJuridisk.INGEN))
        }
        no(OK(RuleJuridisk.INGEN))
    }

private class TestRules(
    payload: TestPayload,
    val getTestRule: (TestEnumRule) -> (TestPayload) -> RuleOutput<TestEnumRule>,
) : TreeExecutor<TestEnumRule, TestPayload>(testTree, payload) {
    override fun getRule(rule: TestEnumRule): (TestPayload) -> RuleOutput<TestEnumRule> =
        getTestRule(rule)
}

private data class TestPayload(override val sykmeldingId: String) : BasePayload

class TreeExecutorTest {

    private val payload = TestPayload("123")

    @Nested
    inner class NormalModeExecution {
        @Test
        fun `shall return OK when tree path is OK`() {
            val executor =
                TestRules(payload) {
                    val result =
                        when (it) {
                            TestEnumRule.RULE_1 -> true
                            TestEnumRule.RULE_2 -> true
                            TestEnumRule.RULE_3 -> true
                        }

                    { _ -> RuleOutput(ruleInputs = emptyMap(), rule = it, ruleResult = result) }
                }

            val result = executor.execute(ExecutionMode.NORMAL)

            assertEquals(result.treeResult.status, RuleStatus.OK)
            assertPath(
                result.rulePath,
                listOf(
                    TestEnumRule.RULE_1 to true,
                    TestEnumRule.RULE_2 to true,
                    TestEnumRule.RULE_3 to true,
                ),
            )
        }

        @Test
        fun `invalid path shall return INVALID`() {
            val executor =
                TestRules(payload) {
                    val result =
                        when (it) {
                            TestEnumRule.RULE_1 -> true
                            TestEnumRule.RULE_2 -> true
                            TestEnumRule.RULE_3 -> false
                        }

                    { _ -> RuleOutput(ruleInputs = emptyMap(), rule = it, ruleResult = result) }
                }

            val result = executor.execute(ExecutionMode.NORMAL)

            assertEquals(result.treeResult.status, RuleStatus.INVALID)
            assertEquals(result.treeResult.juridisk, RuleJuridisk.INGEN)
            assertPath(
                result.rulePath,
                listOf(
                    TestEnumRule.RULE_1 to true,
                    TestEnumRule.RULE_2 to true,
                    TestEnumRule.RULE_3 to false,
                ),
            )
            assertEquals(result.treeResult.getOutcome(), TestEnumRule.Outcomes.CERTAIN_DEATH)
            assertEquals(result.treeResult.getOutcome()?.name, "CERTAIN_DEATH")
        }

        @Test
        fun `manuell path shall return MANUELL`() {
            val executor =
                TestRules(payload) {
                    val result =
                        when (it) {
                            TestEnumRule.RULE_1 -> true
                            TestEnumRule.RULE_2 -> false
                            TestEnumRule.RULE_3 -> false
                        }

                    { _ -> RuleOutput(ruleInputs = emptyMap(), rule = it, ruleResult = result) }
                }

            val result = executor.execute(ExecutionMode.NORMAL)

            assertEquals(result.treeResult.status, RuleStatus.MANUAL_PROCESSING)
            assertPath(
                result.rulePath,
                listOf(TestEnumRule.RULE_1 to true, TestEnumRule.RULE_2 to false),
            )
        }
    }

    @Nested
    inner class PaperModeExecution {

        @Test
        fun `shall return OK when tree path is OK`() {
            val executor =
                TestRules(payload) {
                    val result =
                        when (it) {
                            TestEnumRule.RULE_1 -> true
                            TestEnumRule.RULE_2 -> true
                            TestEnumRule.RULE_3 -> true
                        }

                    { _ -> RuleOutput(ruleInputs = emptyMap(), rule = it, ruleResult = result) }
                }

            val result = executor.execute(ExecutionMode.PAPIR)

            assertEquals(result.treeResult.status, RuleStatus.OK)
            assertPath(
                result.rulePath,
                listOf(
                    TestEnumRule.RULE_1 to true,
                    TestEnumRule.RULE_2 to true,
                    TestEnumRule.RULE_3 to true,
                ),
            )
        }

        @Test
        fun `invalid path shall return MANUELL`() {
            val executor =
                TestRules(payload) {
                    val result =
                        when (it) {
                            TestEnumRule.RULE_1 -> true
                            TestEnumRule.RULE_2 -> true
                            TestEnumRule.RULE_3 -> false
                        }

                    { _ -> RuleOutput(ruleInputs = emptyMap(), rule = it, ruleResult = result) }
                }

            val result = executor.execute(ExecutionMode.PAPIR)

            assertEquals(result.treeResult.status, RuleStatus.MANUAL_PROCESSING)
            assertPath(
                result.rulePath,
                listOf(
                    TestEnumRule.RULE_1 to true,
                    TestEnumRule.RULE_2 to true,
                    TestEnumRule.RULE_3 to false,
                ),
            )
        }

        @Test
        fun `manuell path shall return MANUELL`() {
            val executor =
                TestRules(payload) {
                    val result =
                        when (it) {
                            TestEnumRule.RULE_1 -> true
                            TestEnumRule.RULE_2 -> false
                            TestEnumRule.RULE_3 -> false
                        }

                    { _ -> RuleOutput(ruleInputs = emptyMap(), rule = it, ruleResult = result) }
                }

            val result = executor.execute(ExecutionMode.PAPIR)

            assertEquals(result.treeResult.status, RuleStatus.MANUAL_PROCESSING)
            assertPath(
                result.rulePath,
                listOf(TestEnumRule.RULE_1 to true, TestEnumRule.RULE_2 to false),
            )
        }
    }
}
