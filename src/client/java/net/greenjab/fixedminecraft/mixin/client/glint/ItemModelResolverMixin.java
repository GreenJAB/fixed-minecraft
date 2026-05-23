package net.greenjab.fixedminecraft.mixin.client.glint;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.greenjab.fixedminecraft.render.EnchantGlint;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Credit: Pepperoni-Jabroni */
@Mixin(ItemModelResolver.class)
@Environment(EnvType.CLIENT)
public abstract class ItemModelResolverMixin {

    @Inject(method = "appendItemLayers", at = @At("HEAD"))
    private void setEnchantTheRainbowItemStack(ItemStackRenderState output, ItemStack item, ItemDisplayContext displayContext, Level level,
                                               ItemOwner owner, int seed, CallbackInfo ci) {
        EnchantGlint.setTargetStack(item);
    }
}
