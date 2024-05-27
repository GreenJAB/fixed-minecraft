package net.greenjab.fixedminecraft.StatusEffects

import net.greenjab.fixedminecraft.items.ItemGroupRegistry
import net.greenjab.fixedminecraft.util.identifierOf
import net.greenjab.fixedminecraft.util.register
import net.minecraft.entity.effect.StatusEffect
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.util.Identifier


object StatusRegistry {
    /*val NETHERITE_ANVIL: Block = NetheriteAnvilBlock(blockSettings(Blocks.NETHERITE_BLOCK) {
        strength(15.0F, 1200.0F)
        sounds(BlockSoundGroup.ANVIL)
        pistonBehavior(PistonBehavior.BLOCK)
    })*/

    val REACH: StatusEffect = LongReachEffect()

    fun register() {
        //Registry.register(Registries.STATUS_EFFECT, Identifier("tutorial", "exp"), REACH)
        Registry.register(Registries.STATUS_EFFECT, Identifier("fixedminecraft", "reach"), REACH)
        //Registries.STATUS_EFFECT.register(identifierOf("fixed"), REACH)
    }


}
