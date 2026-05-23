package net.greenjab.fixedminecraft.mixin.night;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.attribute.EnvironmentAttributes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.MoonPhase;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.EnchantedCountIncreaseFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(EnchantedCountIncreaseFunction.class)
public abstract class EnchantedCountIncreaseFunctionMixin {

    @ModifyExpressionValue(method = "run", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/item/enchantment/EnchantmentHelper;getEnchantmentLevel(Lnet/minecraft/core/Holder;Lnet/minecraft/world/entity/LivingEntity;)I"
    ))
    private int moonLooting(int original, @Local(argsOnly = true) LootContext context) {
        Entity entity = context.getOptionalParameter(LootContextParams.ATTACKING_ENTITY);
        ServerLevel world = context.getLevel();
        if (entity instanceof Player) {
            if (world.getBrightness(LightLayer.SKY, entity.blockPosition()) > 10) {
                MoonPhase moonPhase = (world).environmentAttributes().getValue(EnvironmentAttributes.MOON_PHASE, entity.blockPosition());
                if (world.isDarkOutside() && moonPhase.index() == 4) {
                    return original+1;
                }
            }
        }
        return original;
    }
}
