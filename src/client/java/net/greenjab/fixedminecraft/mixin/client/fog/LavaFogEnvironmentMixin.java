package net.greenjab.fixedminecraft.mixin.client.fog;


import com.llamalad7.mixinextras.sugar.Local;
import net.greenjab.fixedminecraft.FixedMinecraft;
import net.greenjab.fixedminecraft.enchanting.FixedMinecraftEnchantmentHelper;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.fog.environment.LavaFogEnvironment;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(LavaFogEnvironment.class)
public abstract class LavaFogEnvironmentMixin {

    @ModifyConstant(method = "setupFog", constant = @Constant(floatValue = 5.0f))
    private float lessLavaFogFireRes(float constant) { return 9f;}

    @ModifyConstant(method = "setupFog", constant = @Constant(floatValue = 1.0f))
    private float lessLavaFog(float constant,
                              @Local(argsOnly = true) Camera camera) {
        int i = 0;
        if (camera.entity() instanceof Player entity) {
            for (ItemStack item : FixedMinecraft.getArmor(entity)) {
                i += FixedMinecraftEnchantmentHelper.enchantLevel(item, "fire_protection");
            }
        }
        return 2.5f + 0.25f*Math.min(2*i,25);
    }

}
