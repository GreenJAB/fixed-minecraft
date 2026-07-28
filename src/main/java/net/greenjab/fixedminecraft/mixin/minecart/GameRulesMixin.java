package net.greenjab.fixedminecraft.mixin.minecart;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.flag.FeatureFlag;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.level.gamerules.GameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(GameRules.class)
public abstract class GameRulesMixin {

    @WrapOperation(method = "<clinit>", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/flag/FeatureFlagSet;of(Lnet/minecraft/world/flag/FeatureFlag;)Lnet/minecraft/world/flag/FeatureFlagSet;"))
    private static FeatureFlagSet createProperRailSpeedGameRule(FeatureFlag flag, Operation<FeatureFlagSet> original){
        return FeatureFlagSet.of();
    }
}
