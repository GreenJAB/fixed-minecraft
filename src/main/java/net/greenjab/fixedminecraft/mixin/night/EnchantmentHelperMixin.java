package net.greenjab.fixedminecraft.mixin.night;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EnchantmentHelper.class)
public class EnchantmentHelperMixin {
    //TODO
    /*@Inject(method = "getLooting", at = @At(value = "HEAD"), cancellable = true)
    private static void moonLooting(LivingEntity entity, CallbackInfoReturnable<Integer> cir) {
        int i = EnchantmentHelper.getEquipmentLevel(Enchantments.LOOTING, entity);
        World world = entity.getWorld();
        if (entity instanceof PlayerEntity) {
            if (world.getLightLevel(LightType.SKY, entity.getBlockPos()) > 10) {
                if (world.isNight() && world.getMoonPhase() == 4) i++;
            }
        }
        cir.setReturnValue(i);
    }*/
}
