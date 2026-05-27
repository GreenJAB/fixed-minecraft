package net.greenjab.fixedminecraft.mixin.client;

import net.greenjab.fixedminecraft.FixedMinecraftClient;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.util.ARGB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(CreativeModeInventoryScreen.class)
public abstract class CreativeModeInventoryScreenMixin {

    @ModifyConstant(method = "extractLabels", constant = @Constant(intValue = -12566464))
    private int ResetToWhiteText(int textColor) {
        if (!FixedMinecraftClient.usingCustomContainers()) return textColor;
        return ARGB.color(200, 200, 200);
    }
}
