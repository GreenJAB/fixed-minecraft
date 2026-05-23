package net.greenjab.fixedminecraft.registry.registries;

import net.greenjab.fixedminecraft.FixedMinecraft;
import net.greenjab.fixedminecraft.registry.other.DispencerMinecartEntity;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

public class EntityTypeRegistry {

    public static final EntityType<DispencerMinecartEntity> DISPENCER_MINECART_ENTITY_TYPE = register(
            keyOf("dispenser_minecart"), EntityType.Builder.of(DispencerMinecartEntity::new, MobCategory.MISC).noLootTable().sized(0.98F, 0.7F).passengerAttachments(0.1875F).clientTrackingRange(8));

    private static <T extends Entity> EntityType<T> register(ResourceKey<EntityType<?>> key, EntityType.Builder<T> type) {
        return Registry.register(BuiltInRegistries.ENTITY_TYPE, key, type.build(key));
    }
    private static ResourceKey<EntityType<?>> keyOf(String id) {
        return ResourceKey.create(Registries.ENTITY_TYPE, FixedMinecraft.id(id));
    }

    public static void registerEntityTypes() {
        System.out.println("register EntityTypes");
    }
}
