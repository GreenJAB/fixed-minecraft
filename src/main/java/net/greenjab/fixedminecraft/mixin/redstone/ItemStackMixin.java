package net.greenjab.fixedminecraft.mixin.redstone;

import net.greenjab.fixedminecraft.registry.other.ContainerTooltipData;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.BundleTooltipData;
import net.minecraft.item.tooltip.TooltipData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;


@Mixin(ItemStack.class)
public class ItemStackMixin {

	@Inject(method = "getTooltipData", at = @At("HEAD"), cancellable = true)
	private void test(CallbackInfoReturnable<Optional<TooltipData>> cir){
        ItemStack itemStack = (ItemStack)(Object) this;
		if (itemStack.getComponents().contains(DataComponentTypes.CONTAINER)) {
                cir.setReturnValue( !itemStack.contains(DataComponentTypes.HIDE_TOOLTIP) && !itemStack.contains(DataComponentTypes.HIDE_ADDITIONAL_TOOLTIP)
                        ? Optional.ofNullable(itemStack.get(DataComponentTypes.CONTAINER)).map(ContainerTooltipData::new)
                        : Optional.empty());
		}
	}
}
