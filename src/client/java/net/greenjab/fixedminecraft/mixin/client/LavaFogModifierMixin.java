package net.greenjab.fixedminecraft.mixin.client;


import com.llamalad7.mixinextras.sugar.Local;
import net.greenjab.fixedminecraft.FixedMinecraft;
import net.greenjab.fixedminecraft.enchanting.FixedMinecraftEnchantmentHelper;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.fog.LavaFogModifier;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(LavaFogModifier.class)
public class LavaFogModifierMixin {

    @ModifyConstant(method = "applyStartEndModifier", constant = @Constant(floatValue = 5.0f))
    private float lessLavaFogFireRes(float constant) { return 9f;}

    @ModifyConstant(method = "applyStartEndModifier", constant = @Constant(floatValue = 1.0f))
    private float lessLavaFog(float constant,
                              @Local(argsOnly = true) Camera camera) {
        int i = 0;
        if (camera.getFocusedEntity() instanceof PlayerEntity entity) {
            for (ItemStack item : FixedMinecraft.getArmor((PlayerEntity) entity)) {
                i += FixedMinecraftEnchantmentHelper.enchantLevel(item, "fire_protection");
            }
        }
        return 2.5f + 0.25f*Math.min(2*i,25);
    }

}
