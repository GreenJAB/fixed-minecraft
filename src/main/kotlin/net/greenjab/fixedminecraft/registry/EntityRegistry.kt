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
    // val BRICK: EntityType<BrickEntity> = Registry.register(Registries.ENTITY_TYPE, Identifier("fixedminecraft", "brick"),
    //    FabricEntityTypeBuilder.create(SpawnGroup.MISC, BrickEntity::new))
    // Object BrickEntity;
   /* var BRICK: EntityType<BrickEntity?>? = Registry.register(Registries.ENTITY_TYPE,
        Identifier("fixedminecraft", "brick"),
        FabricEntityTypeBuilder.create(SpawnGroup.MISC, EntityType.EntityFactory { BrickEntity() })
            .dimensions(EntityDimensions.fixed(0.25f, 0.25f)).trackRangeBlocks(4).trackedUpdateRate(10).build())

    fun register() {
        // Registry.register(Registries.ENTITY_TYPE, Identifier("fixedminecraft", "brick"), BRICK)
    }*/

    /*public static final EntityType<BrickEntity> BRICK = Registry.register(Registries.ENTITY_TYPE,
            new Identifier("fixedminecraft", "brick"),
        FabricEntityTypeBuilder.create(SpawnGroup.MISC, BrickEntity::new)
            .dimensions(EntityDimensions.fixed(0.25f, 0.25f)).trackRangeBlocks(4).trackedUpdateRate(10).build());// */

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
