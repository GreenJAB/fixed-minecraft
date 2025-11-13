package net.greenjab.fixedminecraft.mixin.mobs;

import net.minecraft.block.CreakingHeartBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.attribute.EnvironmentAttribute;
import net.minecraft.world.attribute.WorldEnvironmentAttributeAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(CreakingHeartBlock.class)
public abstract class CreakingHeartBlockMixin {
    @Redirect(method = "enableIfValid", at = @At(value = "INVOKE",
                                                 target = "Lnet/minecraft/world/attribute/WorldEnvironmentAttributeAccess;getAttributeValue(Lnet/minecraft/world/attribute/EnvironmentAttribute;Lnet/minecraft/util/math/BlockPos;)Ljava/lang/Object;"))
    private static Object spawnInDay(WorldEnvironmentAttributeAccess instance, EnvironmentAttribute<?> environmentAttribute,
                                     BlockPos blockPos){
        return true;
    }
}
