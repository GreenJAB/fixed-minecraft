package net.greenjab.fixedminecraft.registry.entity

import net.minecraft.entity.EntityStatuses
import net.minecraft.entity.EntityType
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.mob.BlazeEntity
import net.minecraft.entity.projectile.thrown.SnowballEntity
import net.minecraft.entity.projectile.thrown.ThrownItemEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.particle.ItemStackParticleEffect
import net.minecraft.particle.ParticleEffect
import net.minecraft.particle.ParticleTypes
import net.minecraft.util.hit.EntityHitResult
import net.minecraft.util.hit.HitResult
import net.minecraft.world.World

class BrickEntity : ThrownItemEntity {

    constructor(entityType: EntityType<out BrickEntity?>?, world: World?) :
        super(entityType, world)

    constructor(world: World?, owner: LivingEntity?, stack: ItemStack?) :
        super(EntityType.SNOWBALL, owner, world, stack)

    constructor(world: World?, x: Double, y: Double, z: Double, stack: ItemStack?) :
        super(EntityType.SNOWBALL, x, y, z, world, stack)


    override fun getDefaultItem(): Item {
        return Items.AIR
    }

    private fun getParticleParameters(): ParticleEffect {
        val itemStack = this.stack
        return (if (itemStack.isEmpty) ParticleTypes.ITEM_SNOWBALL else ItemStackParticleEffect(
            ParticleTypes.ITEM,
            itemStack
        )) as ParticleEffect
    }

    override fun handleStatus(status: Byte) {
        if (status == EntityStatuses.PLAY_DEATH_SOUND_OR_ADD_PROJECTILE_HIT_PARTICLES) {
            val particleEffect = this.getParticleParameters()

            for (i in 0..7) {
                world.addParticle(particleEffect, this.x, this.y, this.z, 0.0, 0.0, 0.0)
            }
        }
    }

    override fun onEntityHit(entityHitResult: EntityHitResult) {
        super.onEntityHit(entityHitResult)
        val entity = entityHitResult.entity
        entity.serverDamage(this.damageSources.thrown(this, this.owner), 1.5f)
    }

    override fun onCollision(hitResult: HitResult?) {
        super.onCollision(hitResult)
        if (!world.isClient) {
            world.sendEntityStatus(this, EntityStatuses.PLAY_DEATH_SOUND_OR_ADD_PROJECTILE_HIT_PARTICLES)
            this.discard()
        }
    }
}

