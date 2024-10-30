package net.greenjab.fixedminecraft.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.MerchantScreen;
import net.minecraft.screen.MerchantScreenHandler;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@SuppressWarnings("unchecked")
@Mixin(MerchantScreen.class)
public class MerchantScreenMixin {

    private static final Identifier CHEST_SLOTS_TEXTURE = new Identifier("container/horse/chest_slots");
    @Inject(method = "drawBackground", at = @At(value = "TAIL"))
    private void armorSlotBackground(DrawContext context, float delta, int mouseX, int mouseY, CallbackInfo ci, @Local(ordinal = 2) int i, @Local(ordinal = 3) int j) {
        MerchantScreen MS = (MerchantScreen) (Object)this;
        int l = MS.getScreenHandler().getLevelProgress();
        for (int k = 5-l; k < 4; k++) {
            context.drawGuiTexture(CHEST_SLOTS_TEXTURE, 90, 54, 0, 0, i + 250-1+2, j + 8 + k * 18-1, 18, 18);
        }
    }
}
