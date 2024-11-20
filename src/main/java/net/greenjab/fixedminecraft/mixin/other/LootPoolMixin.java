package net.greenjab.fixedminecraft.mixin.other;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.context.LootContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LootPool.class)
public class LootPoolMixin {

    @ModifyExpressionValue(method = "addGeneratedLoot", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/loot/provider/number/LootNumberProvider;nextInt(Lnet/minecraft/loot/context/LootContext;)I"
    ))
    private int stillLucky(int original, @Local(argsOnly = true) LootContext context) {
        return original + (int)context.getLuck();
    }
}
