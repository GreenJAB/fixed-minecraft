package net.greenjab.fixedminecraft.registry.item

import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.passive.FoxEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.server.world.ServerWorld
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents
import net.minecraft.world.World
import net.minecraft.world.event.GameEvent

class EchoFruitItem(settings: Settings?) : Item(settings) {
    override fun finishUsing(stack: ItemStack, world: World, user: LivingEntity): ItemStack {
        val itemStack = super.finishUsing(stack, world, user)
        if (user is PlayerEntity) {
            if (!world.isClient) {
                val playerEntity = user
                val d = playerEntity.lastDeathPos.get().pos.x.toDouble()
                val e = playerEntity.lastDeathPos.get().pos.y.toDouble()
                val f = playerEntity.lastDeathPos.get().pos.z.toDouble()
                if (user.hasVehicle()) {
                    user.stopRiding()
                }

                val vec3d = user.getPos()
                // if (user.teleport(d, e, f, true)) {
                // if (user.teleport(playerEntity.getLastDeathPos().get().getDimension(),
                if (user.teleport(
                        world as ServerWorld,
                        d, e, f, setOf(), user.getYaw(), user.getPitch()
                    )
                ) {
                    world.emitGameEvent(GameEvent.TELEPORT, vec3d, GameEvent.Emitter.of(user as Entity))
                    val soundCategory: SoundCategory
                    val soundEvent: SoundEvent
                    if (user is FoxEntity) {
                        soundEvent = SoundEvents.ENTITY_FOX_TELEPORT
                        soundCategory = SoundCategory.NEUTRAL
                    } else {
                        soundEvent = SoundEvents.ITEM_CHORUS_FRUIT_TELEPORT
                        soundCategory = SoundCategory.PLAYERS
                    }

                    world.playSound(null as PlayerEntity?, user.getX(), user.getY(), user.getZ(), soundEvent, soundCategory)
                    user.onLanding()


                    playerEntity.itemCooldownManager[this] = 20
                }
            }
        }

        return itemStack
    }
}
