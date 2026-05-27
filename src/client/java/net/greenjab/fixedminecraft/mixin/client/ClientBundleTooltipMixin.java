package net.greenjab.fixedminecraft.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientBundleTooltip;
import net.minecraft.util.Mth;
import net.minecraft.world.item.component.BundleContents;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(ClientBundleTooltip.class)
public abstract class ClientBundleTooltipMixin {

    @Shadow
    @Final
    private BundleContents contents;

    @ModifyConstant(method = {"extractBundleWithItemsTooltip", "slotCount"}, constant = @Constant(intValue = 12))
    private static int moreItemsShown(int constant) { return 64;}

    @ModifyConstant(method = {"extractBundleWithItemsTooltip", "gridSizeY"}, constant = @Constant(intValue = 4, ordinal = 0))
    private int moreItemsPerLine(int constant) { return sizeX(); }

    @ModifyConstant(method = {"extractBundleWithItemsTooltip", "itemGridHeight"}, constant = @Constant(intValue = 24))
    private int smallerGaps(int constant) { return 20; }

    @ModifyArgs(method = "extractBundleWithItemsTooltip", at = @At( value = "INVOKE",
            target = "Lnet/minecraft/client/gui/screens/inventory/tooltip/ClientBundleTooltip;extractSlot(IIILjava/util/List;ILnet/minecraft/client/gui/Font;Lnet/minecraft/client/gui/GuiGraphicsExtractor;)V"))
    private void adjustItemsForSmallerGaps(Args args) {
        args.set(1, (int)args.get(1)-2);
        args.set(2, (int)args.get(2)-2);
    }

    @ModifyExpressionValue(method = "extractBundleWithItemsTooltip", at = @At( value = "INVOKE",
            target = "Lnet/minecraft/client/gui/screens/inventory/tooltip/ClientBundleTooltip;getContentXOffset(I)I", ordinal = 0))
    private int widerUI(int constant) { return sizeX()*20-96; }

    @ModifyConstant(method = "getWidth", constant = @Constant(intValue = 96))
    private int widerUI2(int constant) { return Math.max(96, sizeX()*20); }

    @Unique
    private int sizeX(){
        return Mth.ceil(Mth.sqrt(Math.min(contents.size(),64)));
    }
}
