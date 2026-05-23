package net.greenjab.fixedminecraft.mixin.villager;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.greenjab.fixedminecraft.registry.registries.GameRuleRegistry;
import net.minecraft.world.entity.ai.behavior.HarvestFarmland;
import net.minecraft.world.level.gamerules.GameRule;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(HarvestFarmland.class)
public abstract class HarvestFarmlandMixin {
    @ModifyExpressionValue(method = "checkExtraStartConditions(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/npc/villager/Villager;)Z", at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/world/level/gamerules/GameRules;MOB_GRIEFING:Lnet/minecraft/world/level/gamerules/GameRule;",
            opcode = Opcodes.GETSTATIC
    ))
    public GameRule<Boolean> passiveMobGriefing(GameRule<Boolean> original) {
        return GameRuleRegistry.PEACEFUL_MOB_GRIEFING;
    }
}
