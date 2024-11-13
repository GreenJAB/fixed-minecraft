package net.greenjab.fixedminecraft.registry.item

import net.greenjab.fixedminecraft.StatusEffects.StatusRegistry.INSOMNIA
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.effect.StatusEffectInstance
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.network.packet.s2c.play.GameStateChangeS2CPacket
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.world.World

class PhantomMembraneItem(settings: Settings?) : Item(settings) {
    override fun finishUsing(stack: ItemStack, world: World, user: LivingEntity): ItemStack {
        super.finishUsing(stack, world, user)
        if (user is ServerPlayerEntity) {
            if (!user.hasStatusEffect(INSOMNIA)) {
                user.networkHandler.sendPacket(
                    GameStateChangeS2CPacket(
                        GameStateChangeS2CPacket.ELDER_GUARDIAN_EFFECT,
                        2f
                    )
                )
            }
        }
        if (!world.isClient) {
            user.addStatusEffect(StatusEffectInstance(INSOMNIA, -1, 0, true, false, true))
            user.addStatusEffect(StatusEffectInstance(StatusEffects.BLINDNESS, 400))
        }
        return stack
    }

}
