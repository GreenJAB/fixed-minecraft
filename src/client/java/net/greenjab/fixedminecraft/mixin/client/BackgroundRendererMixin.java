package net.greenjab.fixedminecraft.mixin.client;


import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(BackgroundRenderer.class)
public class BackgroundRendererMixin {

    @ModifyConstant(method = "applyFog", constant = @Constant(floatValue = 3.0f))
    private static float lessLavaFogFireRes(float constant) { return 9f;}

    @ModifyConstant(method = "applyFog", constant = @Constant(floatValue = 1.0f))
    private static float lessLavaFog(float constant, @Local Entity entity) {
        int i = EnchantmentHelper.getProtectionAmount(entity.getArmorItems(), (entity.getDamageSources().lava()));
        return 2.5f + 0.25f*Math.min(i,25);
    }

    @ModifyConstant(method = "applyFog", constant = @Constant(floatValue = 4.0f))
    private static float moreSkyFog(float constant) { return 64f;}
}
