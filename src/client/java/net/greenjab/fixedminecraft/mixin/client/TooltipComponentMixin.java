package net.greenjab.fixedminecraft.mixin.client;

import net.greenjab.fixedminecraft.registry.other.ContainerTooltipData;
import net.greenjab.fixedminecraft.render.ContainerTooltipComponent;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.item.tooltip.TooltipData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TooltipComponent.class)
public interface TooltipComponentMixin {

    @Inject(method = "of(Lnet/minecraft/item/tooltip/TooltipData;)Lnet/minecraft/client/gui/tooltip/TooltipComponent;", at = @At(value = "HEAD"), cancellable = true)
    private static void useContainerTooltip(TooltipData tooltipData, CallbackInfoReturnable<TooltipComponent> cir) {
        if (tooltipData instanceof ContainerTooltipData containerTooltipData) {
            cir.setReturnValue(new ContainerTooltipComponent(containerTooltipData.contents()));
        }
    }
}
