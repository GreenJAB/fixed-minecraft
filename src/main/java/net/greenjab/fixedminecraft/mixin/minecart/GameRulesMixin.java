package net.greenjab.fixedminecraft.mixin.minecart;

import net.minecraft.resource.featuretoggle.FeatureFlag;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.world.rule.GameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(GameRules.class)
public class GameRulesMixin {

    @Redirect(method = "<clinit>", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/resource/featuretoggle/FeatureSet;of(Lnet/minecraft/resource/featuretoggle/FeatureFlag;)Lnet/minecraft/resource/featuretoggle/FeatureSet;"
    ))
    private static FeatureSet createProperRailSpeedGameRule(FeatureFlag original){
        return FeatureSet.empty();
    }
}
