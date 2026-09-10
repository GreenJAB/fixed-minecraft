package net.greenjab.fixedminecraft.mixin.client.glint;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.greenjab.fixedminecraft.render.EnchantGlint;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.item.SelectItemModel;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Credit: Pepperoni-Jabroni */
@Mixin(SelectItemModel.class)
@Environment(EnvType.CLIENT)
public abstract class ItemModelMixin {

    @Inject(method = "update", at = @At("HEAD"))
    private void setEnchantTheRainbowItemStack(ItemStackRenderState output, ItemStack item, ItemModelResolver resolver,
                                               ItemDisplayContext displayContext, ClientLevel level, ItemOwner owner, int seed,
                                               CallbackInfo ci) {
        EnchantGlint.setTargetStack(item);
    }
}
