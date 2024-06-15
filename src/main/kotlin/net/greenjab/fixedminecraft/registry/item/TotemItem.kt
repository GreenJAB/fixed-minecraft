package net.greenjab.fixedminecraft.registry.item

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.ItemUsage
import net.minecraft.sound.SoundEvents
import net.minecraft.util.Hand
import net.minecraft.util.TypedActionResult
import net.minecraft.util.UseAction
import net.minecraft.world.World

// FIXME: Unused?
class TotemItem(settings: Settings) : Item(settings) {

    override fun getUseAction(stack: ItemStack?): UseAction {
        return UseAction.TOOT_HORN
    }
    override fun getMaxUseTime(stack: ItemStack?): Int {
        return 72000
    }
    override fun use(world: World?, user: PlayerEntity, hand: Hand?): TypedActionResult<ItemStack> {
        user.playSound(SoundEvents.ITEM_SPYGLASS_USE, 1.0f, 1.0f)
        return ItemUsage.consumeHeldItem(world, user, hand)
    }
}
