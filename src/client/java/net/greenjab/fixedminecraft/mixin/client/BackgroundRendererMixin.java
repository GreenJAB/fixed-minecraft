package net.greenjab.fixedminecraft.mixin.client;


import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.greenjab.fixedminecraft.FixedMinecraftClient;
import net.greenjab.fixedminecraft.data.ModTags;
import net.greenjab.fixedminecraft.enchanting.FixedMinecraftEnchantmentHelper;
import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.Fog;
import net.minecraft.client.render.FogShape;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;

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


    @Redirect(method = "applyFog", at = @At(value = "NEW", target = "(FFLnet/minecraft/client/render/FogShape;FFFF)Lnet/minecraft/client/render/Fog;"))
    private static Fog paleFog(float f, float g, FogShape fogShape, float h, float i, float j, float k,
                               @Local(argsOnly = true)Camera camera, @Local(ordinal = 0, argsOnly = true) float viewDistance, @Local(argsOnly = true)BackgroundRenderer.FogType fogType) {
        if (fogType == BackgroundRenderer.FogType.FOG_TERRAIN) {
            float palefog = FixedMinecraftClient.INSTANCE.getPaleGardenFog();
            Fog fog = new Fog((float) (3+(f-3) * (1-Math.pow(palefog, 0.5))), 16 + (g - 16) * (1-palefog), FogShape.SPHERE, h, i, j, k);
            if (camera.getFocusedEntity().getWorld().getBiome(camera.getBlockPos()).isIn(ModTags.INSTANCE.getIS_PALE_GARDEN()) ) {
                FixedMinecraftClient.INSTANCE.setPaleGardenFog(Math.min(palefog+0.015f, 1));
                return fog;
            } else {
                FixedMinecraftClient.INSTANCE.setPaleGardenFog(Math.max(palefog-0.015f, 0));

                if (palefog > 0) {
                    return fog;
                }
            }
        }

        return  new Fog(f, g, fogShape, h, i, j, k);
    }

}
