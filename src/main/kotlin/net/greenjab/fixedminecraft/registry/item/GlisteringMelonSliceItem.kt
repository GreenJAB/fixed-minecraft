package net.greenjab.fixedminecraft.registry.item

import net.minecraft.entity.LivingEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.world.World

class GlisteringMelonSliceItem(settings: Settings?) : Item(settings) {
    override fun finishUsing(stack: ItemStack, world: World, user: LivingEntity): ItemStack {
        super.finishUsing(stack, world, user)
        if (!world.isClient) {
            user.health+=2
        }
        return stack
    }
}
