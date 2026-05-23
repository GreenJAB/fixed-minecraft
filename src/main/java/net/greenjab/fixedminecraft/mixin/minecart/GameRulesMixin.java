package net.greenjab.fixedminecraft.mixin.minecart;

import net.minecraft.world.flag.FeatureFlag;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.level.gamerules.GameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(GameRules.class)
public abstract class GameRulesMixin {

    @Redirect(method = "<clinit>", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/flag/FeatureFlagSet;of(Lnet/minecraft/world/flag/FeatureFlag;)Lnet/minecraft/world/flag/FeatureFlagSet;"
    ))
    private static FeatureFlagSet createProperRailSpeedGameRule(FeatureFlag flag){
        return FeatureFlagSet.of();
    }
}
