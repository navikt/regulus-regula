package no.nav.tsm.regulus.regula.executor

import no.nav.tsm.regulus.regula.dsl.Juridisk
import no.nav.tsm.regulus.regula.dsl.TreeOutput

interface RuleExecution<Rules> {
    fun runRules(): Pair<TreeOutput<Rules, RuleResult>, Juridisk>
}
