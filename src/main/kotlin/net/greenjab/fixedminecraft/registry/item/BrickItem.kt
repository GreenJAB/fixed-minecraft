package net.greenjab.fixedminecraft.registry.item

 // import net.greenjab.fixedminecraft.BrickEntity
 import net.greenjab.fixedminecraft.registry.entity.BrickEntity
 import net.minecraft.entity.LivingEntity
 import net.minecraft.entity.player.PlayerEntity
 import net.minecraft.entity.projectile.ProjectileEntity
 import net.minecraft.entity.projectile.thrown.SnowballEntity
 import net.minecraft.item.Item
 import net.minecraft.item.ItemStack
 import net.minecraft.item.ProjectileItem
 import net.minecraft.server.world.ServerWorld
 import net.minecraft.sound.SoundCategory
 import net.minecraft.sound.SoundEvents
 import net.minecraft.stat.Stats
 import net.minecraft.util.ActionResult
 import net.minecraft.util.Hand
 import net.minecraft.util.math.Direction
 import net.minecraft.util.math.Position
 import net.minecraft.world.World

// unused for now, want toggle for it
class BrickItem(settings: Settings) : Item(settings), ProjectileItem {
    var POWER: Float = 1.5f
    override fun use(world: World?, user: PlayerEntity, hand: Hand?): ActionResult {
        val itemStack = user.getStackInHand(hand)
        world!!.playSound(
            null as PlayerEntity?,
            user.x,
            user.y,
            user.z,
            SoundEvents.ENTITY_EGG_THROW,
            SoundCategory.PLAYERS,
            0.5f,
            0.4f / (world.getRandom().nextFloat() * 0.4f + 0.8f)
        )
        /*if (!world.isClient) {
            val brickEntity = BrickEntity(world, user)
            brickEntity.setItem(itemStack)
            brickEntity.setVelocity(user, user.pitch, user.yaw, 0.0f, 1.5f, 1.0f)
            world.spawnEntity(brickEntity)
        }*/
        if (world is ServerWorld) {
            ProjectileEntity.spawnWithVelocity({ world: ServerWorld?, owner: LivingEntity?, stack: ItemStack? ->
                BrickEntity(
                    world,
                    owner,
                    stack
                )
            }, world, itemStack, user, 0.0f, POWER, 1.0f)
        }

        user.incrementStat(Stats.USED.getOrCreateStat(this))
        if (!user.abilities.creativeMode) {
            itemStack.decrement(1)
        }

        return ActionResult.SUCCESS
    }

    override fun createEntity(world: World?, pos: Position?, stack: ItemStack?, direction: Direction?): ProjectileEntity {
        return SnowballEntity(world, pos!!.x, pos!!.y, pos!!.z, stack)
    }
}
