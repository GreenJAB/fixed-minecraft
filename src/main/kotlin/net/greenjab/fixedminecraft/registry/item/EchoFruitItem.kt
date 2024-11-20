package net.greenjab.fixedminecraft.registry.item

import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
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
            val deathOpt = user.lastDeathPos
            if (!world.isClient && deathOpt.isPresent) {
                val deathPos = user.lastDeathPos.get()
                val d = deathPos.pos.x.toDouble()
                val e = deathPos.pos.y.toDouble()
                val f = deathPos.pos.z.toDouble()
                if (user.hasVehicle()) {
                    user.stopRiding()
                }

                val vec3d = user.getPos()
                val serverWorld: ServerWorld? = user.server!!.getWorld(user.lastDeathPos.get().dimension)
                if (user.teleport(serverWorld as ServerWorld, d, e, f, setOf(), user.getYaw(), user.getPitch())) {
                    while (!serverWorld.isSpaceEmpty(user) && user.getY() < serverWorld.topY.toDouble()) {
                        user.setPosition(user.getX(), user.getY() + 1.0, user.getZ())
                    }
                    world.emitGameEvent(GameEvent.TELEPORT, vec3d, GameEvent.Emitter.of(user as Entity))
                    val soundEvent: SoundEvent = SoundEvents.ITEM_CHORUS_FRUIT_TELEPORT
                    val soundCategory: SoundCategory = SoundCategory.PLAYERS

                    world.playSound(null as PlayerEntity?, vec3d.getX(), vec3d.getY(), vec3d.getZ(), soundEvent, soundCategory)
                    user.onLanding()

                    user.itemCooldownManager[this] = 20
                }
            }
        }

        return itemStack
    }
}
