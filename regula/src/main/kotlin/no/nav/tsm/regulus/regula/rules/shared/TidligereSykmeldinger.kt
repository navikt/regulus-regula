package no.nav.tsm.regulus.regula.rules.shared

import no.nav.tsm.regulus.regula.RegulaStatus
import no.nav.tsm.regulus.regula.payload.Diagnose
import no.nav.tsm.regulus.regula.payload.RelevanteMerknader
import no.nav.tsm.regulus.regula.payload.TidligereSykmelding

fun List<TidligereSykmelding>.onlyRelevantWithSameDiagnosis(hoveddiagnose: Diagnose?) =
    this.filter { it.meta.status != RegulaStatus.INVALID }
        .filter {
            it.meta.merknader.isNullOrEmpty() ||
                it.meta.merknader.intersect(RelevanteMerknader.entries).isEmpty()
        }
        .filter { it.hoveddiagnose?.kode != null }
        .filter { it.hoveddiagnose?.kode == hoveddiagnose?.kode }
