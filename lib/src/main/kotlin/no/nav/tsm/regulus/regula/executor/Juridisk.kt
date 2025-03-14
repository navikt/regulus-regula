package no.nav.tsm.regulus.regula.executor

import no.nav.tsm.regulus.regula.juridisk.JuridiskHenvisning

sealed interface Juridisk

data object UtenJuridisk : Juridisk

data class MedJuridisk(val juridiskHenvisning: JuridiskHenvisning) : Juridisk
