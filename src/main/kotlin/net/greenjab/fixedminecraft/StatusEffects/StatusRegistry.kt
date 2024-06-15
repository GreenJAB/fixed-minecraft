package net.greenjab.fixedminecraft.StatusEffects

import net.minecraft.entity.effect.StatusEffect
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.util.Identifier


object StatusRegistry {
    val REACH: StatusEffect = LongReachEffect()

    fun register() {
        Registry.register(Registries.STATUS_EFFECT, Identifier("fixedminecraft", "reach"), REACH)
    }


}
