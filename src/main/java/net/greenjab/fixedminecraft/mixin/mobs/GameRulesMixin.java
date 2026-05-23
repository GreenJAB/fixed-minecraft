package net.greenjab.fixedminecraft.mixin.mobs;

import net.minecraft.world.level.gamerules.GameRules;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(GameRules.class)
public abstract class GameRulesMixin {

    @ModifyArg(method="<clinit>", at = @At( value = "INVOKE", target = "Lnet/minecraft/world/level/gamerules/GameRules;registerBoolean(Ljava/lang/String;Lnet/minecraft/world/level/gamerules/GameRuleCategory;Z)Lnet/minecraft/world/level/gamerules/GameRule;", ordinal = 0 ), slice = @Slice(from =
    @At(value = "CONSTANT", args = "stringValue=mob_explosion_drop_decay"), to =
    @At(value = "FIELD",target = "Lnet/minecraft/world/level/gamerules/GameRules;MOB_EXPLOSION_DROP_DECAY:Lnet/minecraft/world/level/gamerules/GameRule;", opcode = Opcodes.PUTSTATIC)), index = 2)
    private static boolean mobExplosionDecayDefaultFalse(boolean initialValue){
        return false;
    }
}
