package no.nav.tsm.regulus.regula.juridisk

import no.nav.tsm.regulus.regula.RegulaJuridiskHenvisning

internal sealed interface Juridisk

internal data object UtenJuridisk : Juridisk

internal data class MedJuridisk(val juridiskHenvisning: RegulaJuridiskHenvisning) : Juridisk
