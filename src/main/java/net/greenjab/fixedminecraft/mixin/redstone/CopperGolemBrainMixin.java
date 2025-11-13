package net.greenjab.fixedminecraft.mixin.redstone;

import com.google.common.collect.ImmutableList;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.greenjab.fixedminecraft.registry.registries.OtherRegistry;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.passive.CopperGolemBrain;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
@Mixin(CopperGolemBrain.class)
public class CopperGolemBrainMixin {

    @ModifyExpressionValue(method = "<clinit>", at = @At(value = "INVOKE", target = "Lcom/google/common/collect/ImmutableList;of(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;[Ljava/lang/Object;)Lcom/google/common/collect/ImmutableList;"))
    private static ImmutableList<MemoryModuleType<?>> addModules(ImmutableList<MemoryModuleType<?>> original) {
        return ImmutableList.of(
                MemoryModuleType.IS_PANICKING,
                MemoryModuleType.HURT_BY,
                MemoryModuleType.HURT_BY_ENTITY,
                MemoryModuleType.MOBS,
                MemoryModuleType.VISIBLE_MOBS,
                MemoryModuleType.WALK_TARGET,
                MemoryModuleType.LOOK_TARGET,
                MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE,
                MemoryModuleType.PATH,
                MemoryModuleType.GAZE_COOLDOWN_TICKS,
                MemoryModuleType.TRANSPORT_ITEMS_COOLDOWN_TICKS,
                MemoryModuleType.VISITED_BLOCK_POSITIONS,
                MemoryModuleType.UNREACHABLE_TRANSPORT_BLOCK_POSITIONS,
                MemoryModuleType.DOORS_TO_CLOSE,
                MemoryModuleType.NEAREST_BED,
                OtherRegistry.LAST_ITEM_TYPE);
    }

}
