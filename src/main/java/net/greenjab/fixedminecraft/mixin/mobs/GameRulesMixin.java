package net.greenjab.fixedminecraft.mixin.mobs;

import net.minecraft.world.GameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(GameRules.class)
public abstract class GameRulesMixin {

    @ModifyArg(method = "<clinit>", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/GameRules$BooleanRule;create(Z)Lnet/minecraft/world/GameRules$Type;", ordinal = 30))
    private static boolean mobExplosionDecayDefaultFalse(boolean initialValue){
        return false;
    }
}
