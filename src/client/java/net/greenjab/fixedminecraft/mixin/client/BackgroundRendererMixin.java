package net.greenjab.fixedminecraft.mixin.client;


import com.llamalad7.mixinextras.sugar.Local;
import net.greenjab.fixedminecraft.enchanting.FixedMinecraftEnchantmentHelper;
import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import java.util.Iterator;

@Mixin(BackgroundRenderer.class)
public class BackgroundRendererMixin {

    @ModifyConstant(method = "applyFog", constant = @Constant(floatValue = 5.0f))
    private static float lessLavaFogFireRes(float constant) { return 9f;}

    @ModifyConstant(method = "applyFog", constant = @Constant(floatValue = 1.0f))
    private static float lessLavaFog(float constant, @Local Entity entity) {
        //nt i = EnchantmentHelper.getProtectionAmount(entity.getArmorItems(), (entity.getDamageSources().lava()));
        int i = 0;
        for (ItemStack item : ((PlayerEntity) entity).getArmorItems()) {
            i += FixedMinecraftEnchantmentHelper.enchantLevel(item, "fire protection");
        }
        return 2.5f + 0.25f*Math.min(2*i,25);
    }

    @ModifyConstant(method = "applyFog", constant = @Constant(floatValue = 4.0f))
    private static float moreSkyFog(float constant, @Local(ordinal = 0, argsOnly = true) float viewDistance) { return Math.min(64f, viewDistance/2);}
}
