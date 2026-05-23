package net.greenjab.fixedminecraft.mixin.mobs;

import net.minecraft.core.BlockPos;
import net.minecraft.world.attribute.EnvironmentAttribute;
import net.minecraft.world.attribute.EnvironmentAttributeSystem;
import net.minecraft.world.level.block.entity.CreakingHeartBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(CreakingHeartBlockEntity.class)
public abstract class CreakingHeartBlockEntityMixin {
    @Redirect(method = "updateCreakingState", at = @At(value = "INVOKE",
                                                 target = "Lnet/minecraft/world/attribute/EnvironmentAttributeSystem;getValue(Lnet/minecraft/world/attribute/EnvironmentAttribute;Lnet/minecraft/core/BlockPos;)Ljava/lang/Object;"))
    private static Object spawnInDay(EnvironmentAttributeSystem instance, EnvironmentAttribute<?> environmentAttribute,
                                     BlockPos blockPos){
        return true;
    }

    @Redirect(method = "serverTick", at = @At(value = "INVOKE",
                                                 target = "Lnet/minecraft/world/attribute/EnvironmentAttributeSystem;getValue(Lnet/minecraft/world/attribute/EnvironmentAttribute;Lnet/minecraft/core/BlockPos;)Ljava/lang/Object;"))
    private static Object spawnInDay2(EnvironmentAttributeSystem instance, EnvironmentAttribute<?> environmentAttribute,
                                     BlockPos blockPos){
        return true;
    }
}
