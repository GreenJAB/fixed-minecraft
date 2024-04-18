package net.greenjab.fixedminecraft.items.map_book

import net.greenjab.fixedminecraft.network.SyncHandler
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Hand
import net.minecraft.util.TypedActionResult
import net.minecraft.world.World

class MapBookItem(settings: Settings?) : Item(settings) {
    override fun use(world: World?, user: PlayerEntity?, hand: Hand?): TypedActionResult<ItemStack> {
        if (world != null && !world.isClient()) {
            SyncHandler.onOpenMapBook(user as ServerPlayerEntity, user.getStackInHand(hand))
        }
        return super.use(world, user, hand)
    }
}