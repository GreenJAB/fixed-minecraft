package net.greenjab.fixedminecraft.registry.item

import net.greenjab.fixedminecraft.StatusEffects.StatusRegistry.INSOMNIA
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.effect.StatusEffectInstance
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.network.packet.s2c.play.GameStateChangeS2CPacket
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.world.World

class GlisteringMelonSliceItem(settings: Settings?) : Item(settings) {
    override fun finishUsing(stack: ItemStack, world: World, user: LivingEntity): ItemStack {
        super.finishUsing(stack, world, user)
        if (!world.isClient) {
            user.health+=2;
        }
        return stack
    }
}
