package net.greenjab.fixedminecraft.mixin.night;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.function.EnchantedCountIncreaseLootFunction;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.LightType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(EnchantedCountIncreaseLootFunction.class)
public class EnchantmentHelperMixin {

    @ModifyExpressionValue(method = "process", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/enchantment/EnchantmentHelper;getEquipmentLevel(Lnet/minecraft/registry/entry/RegistryEntry;Lnet/minecraft/entity/LivingEntity;)I"
    ))
    private int moonLooting(int original, @Local(argsOnly = true) LootContext context) {
        Entity entity = context.get(LootContextParameters.ATTACKING_ENTITY);
        ServerWorld world = context.getWorld();
        if (entity instanceof PlayerEntity) {
            if (world.getLightLevel(LightType.SKY, entity.getBlockPos()) > 10) {
                if (world.isNight() && world.getMoonPhase() == 4) {
                    return original+1;
                }
            }
        }
        return original;
    }
}
