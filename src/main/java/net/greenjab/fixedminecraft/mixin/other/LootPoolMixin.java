package net.greenjab.fixedminecraft.mixin.other;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootPool;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LootPool.class)
public abstract class LootPoolMixin {

    @ModifyExpressionValue(method = "addRandomItems", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/storage/loot/providers/number/NumberProvider;getInt(Lnet/minecraft/world/level/storage/loot/LootContext;)I"
    ))
    private int stillLucky(int original, @Local(argsOnly = true) LootContext context) {
        return original + (int)context.getLuck();
    }
}
