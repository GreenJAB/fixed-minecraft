package net.greenjab.fixedminecraft.StatusEffects

import net.minecraft.entity.effect.StatusEffect
import net.minecraft.entity.effect.StatusEffectCategory
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.registry.entry.RegistryEntry
import net.minecraft.util.Identifier

object StatusRegistry {
    val AWKWARD: RegistryEntry<StatusEffect> = registerStatusEffect("awkward", AwkwardEffect())
    val REACH: RegistryEntry<StatusEffect> = registerStatusEffect("reach", LongReachEffect())
    val INSOMNIA: RegistryEntry<StatusEffect> = registerStatusEffect("insomnia", InsomniaEffect())

    fun registerStatusEffect(name: String, statusEffect: StatusEffect): RegistryEntry<StatusEffect> {
        return Registry.registerReference(Registries.STATUS_EFFECT, Identifier.of("fixedminecraft", name), statusEffect)
    }
}
