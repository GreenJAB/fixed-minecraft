package net.greenjab.fixedminecraft.mixin.mobs;

import net.minecraft.world.rule.GameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(GameRules.class)
public abstract class GameRulesMixin {

    @ModifyArg(method = "<clinit>", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/rule/GameRules;registerBooleanRule(Ljava/lang/String;Lnet/minecraft/world/rule/GameRuleCategory;Z)Lnet/minecraft/world/rule/GameRule;", ordinal = 24))
    private static boolean mobExplosionDecayDefaultFalse(boolean initialValue){
        return false;
    }
}
