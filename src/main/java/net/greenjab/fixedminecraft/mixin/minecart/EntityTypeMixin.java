package net.greenjab.fixedminecraft.mixin.minecart;

import net.greenjab.fixedminecraft.registry.other.FixedFurnaceMinecartEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityType.class)
public class EntityTypeMixin{

    @Inject(method = "register(Ljava/lang/String;Lnet/minecraft/entity/EntityType$Builder;)Lnet/minecraft/entity/EntityType;", at = @At(value = "HEAD"), cancellable = true)
    private static void useFixedFurnaceMinecarts(String id, EntityType.Builder<?> type, CallbackInfoReturnable<EntityType<?>> cir) {
        if (id.contains("furnace_minecart")) {
            cir.setReturnValue(register(
                    EntityType.Builder.create(FixedFurnaceMinecartEntity::new, SpawnGroup.MISC)
                            .dropsNothing()
                            .dimensions(0.98F, 0.7F)
                            .passengerAttachments(0.1875F)
                            .maxTrackingRange(8)
            ));
            cir.cancel();
        }
    }

    @Unique
    private static <T extends Entity> EntityType<T> register(EntityType.Builder<T> type) {
        return register(keyOf("furnace_minecart"), type);
    }
    @Unique
    private static RegistryKey<EntityType<?>> keyOf(String id) {
        return RegistryKey.of(RegistryKeys.ENTITY_TYPE, Identifier.ofVanilla(id));
    }
    @Unique
    private static <T extends Entity> EntityType<T> register(RegistryKey<EntityType<?>> key, EntityType.Builder<T> type) {
        return Registry.register(Registries.ENTITY_TYPE, key, type.build(key));
    }
}
