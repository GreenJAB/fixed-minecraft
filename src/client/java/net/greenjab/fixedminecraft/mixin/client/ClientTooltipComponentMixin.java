package net.greenjab.fixedminecraft.mixin.client;

import net.greenjab.fixedminecraft.registry.other.ContainerTooltipData;
import net.greenjab.fixedminecraft.render.ContainerTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientTooltipComponent.class)
public interface ClientTooltipComponentMixin {

    @Inject(method = "create(Lnet/minecraft/world/inventory/tooltip/TooltipComponent;)Lnet/minecraft/client/gui/screens/inventory/tooltip/ClientTooltipComponent;", at = @At(value = "HEAD"), cancellable = true)
    private static void useContainerTooltip(TooltipComponent component, CallbackInfoReturnable<ClientTooltipComponent> cir) {
        if (component instanceof ContainerTooltipData(net.minecraft.world.item.component.ItemContainerContents contents)) {
            cir.setReturnValue(new ContainerTooltipComponent(contents));
        }
    }
}
