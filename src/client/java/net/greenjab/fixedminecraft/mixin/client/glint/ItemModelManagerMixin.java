package net.greenjab.fixedminecraft.mixin.client.glint;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.greenjab.fixedminecraft.render.EnchantGlint;
import net.minecraft.client.item.ItemModelManager;
import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Credit: Pepperoni-Jabroni */
@Mixin(ItemModelManager.class)
@Environment(EnvType.CLIENT)
public class ItemModelManagerMixin {

    @Inject(method = "update", at = @At("HEAD"))
    private void setEnchantTheRainbowItemStack(ItemRenderState renderState, ItemStack stack, ItemDisplayContext displayContext, World world,
                                               LivingEntity entity, int seed, CallbackInfo ci) {
        EnchantGlint.setTargetStack(stack);
    }
}
