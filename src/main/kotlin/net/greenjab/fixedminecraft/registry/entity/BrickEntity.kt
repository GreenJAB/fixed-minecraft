package net.greenjab.fixedminecraft.registry.entity

//import kotlin.net.greenjab.fixedminecraft.registry.EntityRegistry
import net.minecraft.entity.EntityStatuses
import net.minecraft.entity.EntityType
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.projectile.thrown.ThrownItemEntity
import net.minecraft.item.Item
import net.minecraft.item.Items
import net.minecraft.particle.ItemStackParticleEffect
import net.minecraft.particle.ParticleTypes
import net.minecraft.util.hit.EntityHitResult
import net.minecraft.util.hit.HitResult
import net.minecraft.world.World


class BrickEntity : ThrownItemEntity {
    constructor(entityType: EntityType<out BrickEntity?>?, world: World?) : super(entityType, world)

    constructor(world: World?, owner: LivingEntity?) : super(EntityType.SNOWBALL, owner, world)
    constructor(world: World, x: Double, y: Double, z: Double) : super(EntityType.SNOWBALL,x, y, z, world)


    override fun handleStatus(status: Byte) {
        if (status == EntityStatuses.PLAY_DEATH_SOUND_OR_ADD_PROJECTILE_HIT_PARTICLES) {
            val d = 0.08

            for (i in 0..7) {
                world.addParticle(
                    ItemStackParticleEffect(ParticleTypes.ITEM, this.stack),
                    this.x,
                    this.y,
                    this.z,
                    (random.nextFloat().toDouble() - 0.5) * 0.08,
                    (random.nextFloat().toDouble() - 0.5) * 0.08,
                    (random.nextFloat().toDouble() - 0.5) * 0.08
                )
            }
        }
    }

    override fun onEntityHit(entityHitResult: EntityHitResult) {
        super.onEntityHit(entityHitResult)
        entityHitResult.entity.damage(this.damageSources.thrown(this, this.owner), 1.5f)
    }

    override fun onCollision(hitResult: HitResult) {
        super.onCollision(hitResult)
        if (!world.isClient) {
                        world.sendEntityStatus(this, EntityStatuses.PLAY_DEATH_SOUND_OR_ADD_PROJECTILE_HIT_PARTICLES)
            this.discard()
        }
    }

    override fun getDefaultItem(): Item {
        return Items.AIR
    }
}

