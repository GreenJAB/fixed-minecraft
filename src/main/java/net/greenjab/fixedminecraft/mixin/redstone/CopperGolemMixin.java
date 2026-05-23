package net.greenjab.fixedminecraft.mixin.redstone;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.greenjab.fixedminecraft.registry.registries.MemoryRegistry;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.animal.golem.CopperGolem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(CopperGolem.class)
public abstract class CopperGolemMixin {

    @ModifyExpressionValue(method = "makeBrain", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/ai/Brain$Provider;makeBrain(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/entity/ai/Brain$Packed;)Lnet/minecraft/world/entity/ai/Brain;"
    ))
    private <E extends LivingEntity> Brain<E> addMemories(Brain<E> original){
        original.registerMemory(MemoryModuleType.NEAREST_BED);
        original.registerMemory(MemoryRegistry.LAST_ITEM_TYPE);
        return original;
    }
}
