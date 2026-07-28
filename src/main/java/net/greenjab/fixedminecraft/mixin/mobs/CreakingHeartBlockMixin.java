package net.greenjab.fixedminecraft.mixin.mobs;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.attribute.EnvironmentAttribute;
import net.minecraft.world.attribute.EnvironmentAttributeSystem;
import net.minecraft.world.level.block.CreakingHeartBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(CreakingHeartBlock.class)
public abstract class CreakingHeartBlockMixin {
    @WrapOperation(method = "updateState", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/attribute/EnvironmentAttributeSystem;getValue(Lnet/minecraft/world/attribute/EnvironmentAttribute;Lnet/minecraft/core/BlockPos;)Ljava/lang/Object;"))
    private static Object spawnInDay(EnvironmentAttributeSystem instance, EnvironmentAttribute<?> environmentAttribute, BlockPos pos, Operation<Object> original){
        return true;
    }
}
