package net.greenjab.fixedminecraft.registry

import net.greenjab.fixedminecraft.registry.entity.BrickEntity
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityType
import net.minecraft.entity.SpawnGroup
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.world.World

@Suppress("MemberVisibilityCanBePrivate")
object EntityRegistry {

    val BRICK: EntityType<BrickEntity?> = register("brick",
        EntityType.Builder.create(
            { entityType: EntityType<BrickEntity?>?, world: World? -> BrickEntity(entityType, world)}
            //BrickEntity()
            //BrickEntity.
            , SpawnGroup.MISC).setDimensions(0.25f, 0.25f).maxTrackingRange(4).trackingTickInterval(10)
    )

    private fun <T : Entity?> register(id: String, type: EntityType.Builder<T>): EntityType<T> {
        return Registry.register(Registries.ENTITY_TYPE, id, type.build(id))
    }
}
