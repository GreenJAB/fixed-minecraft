package net.greenjab.fixedminecraft.mixin.minecart;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.greenjab.fixedminecraft.registry.other.FixedFurnaceMinecartEntity;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.MobCategory;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(EntityTypes.class)
public abstract class EntityTypesMixin {

    @WrapOperation(method="<clinit>", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/EntityTypes;register(Lnet/minecraft/resources/ResourceKey;Lnet/minecraft/world/entity/EntityType$Builder;)Lnet/minecraft/world/entity/EntityType;"), slice = @Slice(from =
    @At(value = "FIELD", target = "Lnet/minecraft/world/entity/EntityTypeIds;FURNACE_MINECART:Lnet/minecraft/resources/ResourceKey;", opcode = Opcodes.GETSTATIC), to =
    @At(value = "FIELD",target = "Lnet/minecraft/world/entity/EntityTypes;FURNACE_MINECART:Lnet/minecraft/world/entity/EntityType;", opcode = Opcodes.PUTSTATIC)))
    private static <T extends Entity> EntityType<T> throwableBrick(ResourceKey<EntityType<?>> id, EntityType.Builder<T> builder, Operation<EntityType<T>> original) {
        return original.call(id, EntityType.Builder.of(FixedFurnaceMinecartEntity::new, MobCategory.MISC)
                .noLootTable()
                .sized(0.98F, 0.7F)
                .passengerAttachments(0.1875F)
                .clientTrackingRange(8));}

}
