package no.nav.tsm.regulus.regula.dsl

import java.time.ZonedDateTime
import no.nav.tsm.regulus.regula.JuridiskHenvisning
import no.nav.tsm.regulus.regula.JuridiskHenvisningLovverk
import no.nav.tsm.regulus.regula.JuridiskUtfall
import no.nav.tsm.regulus.regula.RegulaJuridiskVurdering

internal enum class RuleJuridisk(val juridiskHenvisning: JuridiskHenvisning?) {
    INGEN(null),
    FOLKETRYGDLOVEN_8_3_1(
        JuridiskHenvisning(
            lovverk = JuridiskHenvisningLovverk.FOLKETRYGDLOVEN,
            paragraf = "8-3",
            ledd = 1,
            punktum = null,
            bokstav = null,
        )
    ),
    FOLKETRYGDLOVEN_8_4(
        JuridiskHenvisning(
            lovverk = JuridiskHenvisningLovverk.FOLKETRYGDLOVEN,
            paragraf = "8-4",
            ledd = 1,
            punktum = null,
            bokstav = null,
        )
    ),
    FOLKETRYGDLOVEN_8_7(
        JuridiskHenvisning(
            lovverk = JuridiskHenvisningLovverk.FOLKETRYGDLOVEN,
            paragraf = "8-7",
            ledd = null,
            punktum = null,
            bokstav = null,
        )
    ),
    FOLKETRYGDLOVEN_8_7_1(
        JuridiskHenvisning(
            lovverk = JuridiskHenvisningLovverk.FOLKETRYGDLOVEN,
            paragraf = "8-7",
            ledd = 1,
            punktum = null,
            bokstav = null,
        )
    ),
    FOLKETRYGDLOVEN_8_7_1_1(
        JuridiskHenvisning(
            lovverk = JuridiskHenvisningLovverk.FOLKETRYGDLOVEN,
            paragraf = "8-7",
            ledd = 1,
            punktum = 1,
            bokstav = null,
        )
    ),
    FOLKETRYGDLOVEN_8_7_2_2(
        JuridiskHenvisning(
            lovverk = JuridiskHenvisningLovverk.FOLKETRYGDLOVEN,
            paragraf = "8-7",
            ledd = 2,
            punktum = 2,
            bokstav = null,
        )
    ),
}

internal fun TreeOutput<*>.toRegulaJuridisk(
    pasientIdent: String,
    vurdert: ZonedDateTime,
): RegulaJuridiskVurdering? {
    val juridisk = this.treeResult.juridisk
    if (juridisk == RuleJuridisk.INGEN || juridisk.juridiskHenvisning == null) return null

    return RegulaJuridiskVurdering(
        henvisning = juridisk.juridiskHenvisning,
        utfall = this.treeResult.status.toJuridiskUtfall(),
        input = this.ruleInputs,
        fodselsnummer = pasientIdent,
        tidsstempel = vurdert,
    )
}

internal fun RuleStatus.toJuridiskUtfall() =
    when (this) {
        RuleStatus.OK -> JuridiskUtfall.VILKAR_OPPFYLT
        RuleStatus.INVALID -> JuridiskUtfall.VILKAR_IKKE_OPPFYLT
        RuleStatus.MANUAL_PROCESSING -> JuridiskUtfall.VILKAR_UAVKLART
    }
