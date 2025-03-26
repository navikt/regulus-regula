package no.nav.tsm.regulus.regula.juridisk

internal sealed interface Juridisk

internal data object UtenJuridisk : Juridisk

internal data class MedJuridisk(val juridiskHenvisning: JuridiskHenvisning) : Juridisk
