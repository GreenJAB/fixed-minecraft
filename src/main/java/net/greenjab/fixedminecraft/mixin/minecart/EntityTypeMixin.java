package net.greenjab.fixedminecraft.mixin.minecart;

import net.greenjab.fixedminecraft.registry.other.FixedFurnaceMinecartEntity;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityType.class)
public abstract class EntityTypeMixin{

    @Inject(method = "register(Ljava/lang/String;Lnet/minecraft/world/entity/EntityType$Builder;)Lnet/minecraft/world/entity/EntityType;", at = @At(value = "HEAD"), cancellable = true)
    private static void useFixedFurnaceMinecarts(String vanillaId, EntityType.Builder<?> builder, CallbackInfoReturnable<EntityType<?>> cir) {
        if (vanillaId.contains("furnace_minecart")) {
            cir.setReturnValue(register(
                    EntityType.Builder.of(FixedFurnaceMinecartEntity::new, MobCategory.MISC)
                            .noLootTable()
                            .sized(0.98F, 0.7F)
                            .passengerAttachments(0.1875F)
                            .clientTrackingRange(8)
            ));
            cir.cancel();
        }
    }

    @Unique
    private static <T extends Entity> EntityType<T> register(EntityType.Builder<T> type) {
        return register(keyOf("furnace_minecart"), type);
    }
    @Unique
    private static ResourceKey<EntityType<?>> keyOf(String id) {
        return ResourceKey.create(Registries.ENTITY_TYPE, Identifier.withDefaultNamespace(id));
    }
    @Unique
    private static <T extends Entity> EntityType<T> register(ResourceKey<EntityType<?>> key, EntityType.Builder<T> type) {
        return Registry.register(BuiltInRegistries.ENTITY_TYPE, key, type.build(key));
    }
}
