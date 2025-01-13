package net.greenjab.fixedminecraft.StatusEffects

import net.minecraft.entity.effect.StatusEffect
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.util.Identifier

object StatusRegistry {
    val AWKWARD: StatusEffect = AwkwardEffect()
    val REACH: StatusEffect = LongReachEffect()
    val INSOMNIA: StatusEffect = InsomniaEffect()

    fun register() {
        Registry.register(Registries.STATUS_EFFECT, Identifier.of("fixedminecraft", "awkward"), AWKWARD)
        Registry.register(Registries.STATUS_EFFECT, Identifier.of("fixedminecraft", "reach"), REACH)
        Registry.register(Registries.STATUS_EFFECT, Identifier.of("fixedminecraft", "insomnia"), INSOMNIA)
    }
}
