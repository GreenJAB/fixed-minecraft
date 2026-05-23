package net.greenjab.fixedminecraft.mixin.client.villager;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.MerchantScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MerchantScreen.class)
public abstract class MerchantScreenMixin {
    @Unique
    private static final Identifier CHEST_SLOTS_TEXTURE = Identifier.parse("container/horse/chest_slots");

    @Inject(method = "extractBackground", at = @At(value = "TAIL"))
    private void armorSlotBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a, CallbackInfo ci,
                                     @Local(ordinal = 2) int xo,
                                     @Local(ordinal = 3) int yo) {
        MerchantScreen MS = (MerchantScreen) (Object)this;
        int l = MS.getMenu().getTraderLevel();
        for (int k = 5-l; k < 4; k++) {
            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, CHEST_SLOTS_TEXTURE, 90, 54, 0, 0, xo + 250 - 1 + 2, yo + 8 + k * 18 - 1, 18, 18);
        }
    }
}
