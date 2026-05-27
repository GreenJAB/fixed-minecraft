package net.greenjab.fixedminecraft.mixin.redstone;

import net.greenjab.fixedminecraft.registry.other.ContainerTooltipData;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.component.TypedEntityData;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import java.util.List;
import java.util.Optional;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {

	@Inject(method = "getTooltipImage", at = @At("HEAD"), cancellable = true)
	private void showItemsInContainers(CallbackInfoReturnable<Optional<TooltipComponent>> cir){
        ItemStack itemStack = (ItemStack)(Object) this;
        if (itemStack.getComponents().has(DataComponents.CONTAINER)) {
            TooltipDisplay tooltipDisplayComponent = itemStack.getOrDefault(DataComponents.TOOLTIP_DISPLAY, TooltipDisplay.DEFAULT);
            cir.setReturnValue(!tooltipDisplayComponent.shows(DataComponents.CONTAINER)
                    ? Optional.empty()
                    : Optional.ofNullable(itemStack.get(DataComponents.CONTAINER)).map(ContainerTooltipData::new));
        }
        if (itemStack.getComponents().has(DataComponents.BLOCK_ENTITY_DATA)) {
            TypedEntityData<BlockEntityType<?>> data = itemStack.getComponents().get(DataComponents.BLOCK_ENTITY_DATA);
            if (data != null && data.type() == BlockEntityType.BRUSHABLE_BLOCK) {
                ItemStack item = data.copyTagWithoutId().read("item", ItemStack.CODEC).orElse(ItemStack.EMPTY);
                if (item!=ItemStack.EMPTY) {
                    ItemContainerContents toolTip = ItemContainerContents.fromItems(List.of(item));
                    cir.setReturnValue(Optional.ofNullable(toolTip).map(ContainerTooltipData::new));
                }
            }
        }
	}
}
