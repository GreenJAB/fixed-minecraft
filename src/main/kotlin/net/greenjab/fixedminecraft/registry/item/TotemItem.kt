package net.greenjab.fixedminecraft.registry.item

import net.greenjab.fixedminecraft.registry.GameruleRegistry.Require_Totem_Use
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.ItemUsage
import net.minecraft.item.consume.UseAction
import net.minecraft.server.world.ServerWorld
import net.minecraft.sound.SoundEvents
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.world.World

// unused for now, want toggle for it
class TotemItem(settings: Settings) : Item(settings) {

    override fun getUseAction(stack: ItemStack?): UseAction {
        return UseAction.TOOT_HORN
    }
    override fun getMaxUseTime(stack: ItemStack?, user: LivingEntity): Int {
        return 72000
    }
    override fun use(world: World?, user: PlayerEntity, hand: Hand?): ActionResult {
        // client doesn't recieve gamerule status
        if (world != null) {
            if (world is ServerWorld) {
                if (world.gameRules.getBoolean(Require_Totem_Use)) {
                    user.playSound(SoundEvents.ITEM_SPYGLASS_USE, 1.0f, 1.0f)
                    return ItemUsage.consumeHeldItem(world, user, hand)
                }
            }
        }
        return ActionResult.PASS
    }
}
