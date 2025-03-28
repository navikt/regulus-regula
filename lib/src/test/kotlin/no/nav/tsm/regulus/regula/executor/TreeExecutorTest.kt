package no.nav.tsm.regulus.regula.executor

import kotlin.test.Test
import no.nav.tsm.regulus.regula.dsl.RuleOutput
import no.nav.tsm.regulus.regula.dsl.tree
import no.nav.tsm.regulus.regula.juridisk.JuridiskHenvisning
import no.nav.tsm.regulus.regula.juridisk.Lovverk
import no.nav.tsm.regulus.regula.juridisk.MedJuridisk
import no.nav.tsm.regulus.regula.rules.trees.assertPath
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Nested

private enum class TestEnumRule {
    RULE_1,
    RULE_2,
    RULE_3;

    enum class Outcomes(
        override val rule: String,
        override val status: RuleStatus,
        override val messageForSender: String,
        override val messageForUser: String,
    ) : RuleOutcome {
        ITS_NOT_LOOKING_GOOD(
            rule = "RULE_1",
            status = RuleStatus.INVALID,
            messageForSender = "Sender",
            messageForUser = "User",
        ),
        CERTAIN_DEATH(
            rule = "RULE_2",
            status = RuleStatus.OK,
            messageForSender = "Sender",
            messageForUser = "User",
        ),
    }
}

private val testTree =
    tree<TestEnumRule>(TestEnumRule.RULE_1) {
        yes(TestEnumRule.RULE_2) {
            yes(TestEnumRule.RULE_3) {
                yes(RuleStatus.OK)
                no(RuleStatus.INVALID, TestEnumRule.Outcomes.CERTAIN_DEATH)
            }
            no(RuleStatus.MANUAL_PROCESSING, TestEnumRule.Outcomes.ITS_NOT_LOOKING_GOOD)
        }
        no(RuleStatus.OK)
    } to
        MedJuridisk(
            JuridiskHenvisning(
                lovverk = Lovverk.FOLKETRYGDLOVEN,
                paragraf = "8-4",
                ledd = 1,
                punktum = null,
                bokstav = null,
            )
        )

private class ArbeidsuforhetRules(
    val payload: TestPayload,
    mode: ExecutionMode,
    val getTestRule: (TestEnumRule) -> (TestPayload) -> RuleOutput<TestEnumRule>,
) : TreeExecutor<TestEnumRule, TestPayload>(testTree, payload, mode) {
    override fun getRule(rule: TestEnumRule): (TestPayload) -> RuleOutput<TestEnumRule> =
        getTestRule(rule)
}

private data class TestPayload(override val sykmeldingId: String) : BasePayload

class TreeExecutorTest {

    @Nested
    inner class NormalModeExecution {
        @Test
        fun `shall return OK when tree path is OK`() {
            val payload = TestPayload("123")
            val executor =
                ArbeidsuforhetRules(payload, ExecutionMode.NORMAL) {
                    val result =
                        when (it) {
                            TestEnumRule.RULE_1 -> true
                            TestEnumRule.RULE_2 -> true
                            TestEnumRule.RULE_3 -> true
                        }

                    { _ -> RuleOutput(ruleInputs = emptyMap(), rule = it, ruleResult = result) }
                }

            val (result) = executor.execute()

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
            val payload = TestPayload("123")
            val executor =
                ArbeidsuforhetRules(payload, ExecutionMode.NORMAL) {
                    val result =
                        when (it) {
                            TestEnumRule.RULE_1 -> true
                            TestEnumRule.RULE_2 -> true
                            TestEnumRule.RULE_3 -> false
                        }

                    { _ -> RuleOutput(ruleInputs = emptyMap(), rule = it, ruleResult = result) }
                }

            val (result) = executor.execute()

            assertEquals(result.treeResult.status, RuleStatus.INVALID)
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
            val payload = TestPayload("123")
            val executor =
                ArbeidsuforhetRules(payload, ExecutionMode.NORMAL) {
                    val result =
                        when (it) {
                            TestEnumRule.RULE_1 -> true
                            TestEnumRule.RULE_2 -> false
                            TestEnumRule.RULE_3 -> false
                        }

                    { _ -> RuleOutput(ruleInputs = emptyMap(), rule = it, ruleResult = result) }
                }

            val (result) = executor.execute()

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
            val payload = TestPayload("123")
            val executor =
                ArbeidsuforhetRules(payload, ExecutionMode.PAPIR) {
                    val result =
                        when (it) {
                            TestEnumRule.RULE_1 -> true
                            TestEnumRule.RULE_2 -> true
                            TestEnumRule.RULE_3 -> true
                        }

                    { _ -> RuleOutput(ruleInputs = emptyMap(), rule = it, ruleResult = result) }
                }

            val (result) = executor.execute()

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
            val payload = TestPayload("123")
            val executor =
                ArbeidsuforhetRules(payload, ExecutionMode.PAPIR) {
                    val result =
                        when (it) {
                            TestEnumRule.RULE_1 -> true
                            TestEnumRule.RULE_2 -> true
                            TestEnumRule.RULE_3 -> false
                        }

                    { _ -> RuleOutput(ruleInputs = emptyMap(), rule = it, ruleResult = result) }
                }

            val (result) = executor.execute()

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
            val payload = TestPayload("123")
            val executor =
                ArbeidsuforhetRules(payload, ExecutionMode.PAPIR) {
                    val result =
                        when (it) {
                            TestEnumRule.RULE_1 -> true
                            TestEnumRule.RULE_2 -> false
                            TestEnumRule.RULE_3 -> false
                        }

                    { _ -> RuleOutput(ruleInputs = emptyMap(), rule = it, ruleResult = result) }
                }

            val (result) = executor.execute()

            assertEquals(result.treeResult.status, RuleStatus.MANUAL_PROCESSING)
            assertPath(
                result.rulePath,
                listOf(TestEnumRule.RULE_1 to true, TestEnumRule.RULE_2 to false),
            )
        }
    }
}
