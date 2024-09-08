package net.greenjab.fixedminecraft.registry.item

 import net.greenjab.fixedminecraft.registry.entity.BrickEntity
//import net.greenjab.fixedminecraft.BrickEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.stat.Stats
import net.minecraft.util.Hand
import net.minecraft.util.TypedActionResult
import net.minecraft.world.World

// unused for now, want toggle for it
class BrickItem(settings: Settings) : Item(settings) {

    override fun use(world: World?, user: PlayerEntity, hand: Hand?): TypedActionResult<ItemStack> {
        val itemStack = user.getStackInHand(hand)
        world!!.playSound(
            null as PlayerEntity?,
            user.x,
            user.y,
            user.z,
            SoundEvents.ENTITY_EGG_THROW,
            SoundCategory.PLAYERS,
            0.5f,
            0.4f / (world!!.getRandom().nextFloat() * 0.4f + 0.8f)
        )
        user.itemCooldownManager[this] = 20;
        if (!world!!.isClient) {
            val brickEntity = BrickEntity(world, user)
            brickEntity.setItem(itemStack)
            brickEntity.setVelocity(user, user.pitch, user.yaw, 0.0f, 1.5f, 1.0f)
            world!!.spawnEntity(brickEntity)
        }

        user.incrementStat(Stats.USED.getOrCreateStat(this))
        if (!user.abilities.creativeMode) {
            itemStack.decrement(1)
        }

        return TypedActionResult.success(itemStack, world!!.isClient())
    }
}
