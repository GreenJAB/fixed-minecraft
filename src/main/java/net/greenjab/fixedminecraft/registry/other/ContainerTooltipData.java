package net.greenjab.fixedminecraft.registry.other;

import net.minecraft.component.type.ContainerComponent;
import net.minecraft.item.tooltip.TooltipData;

public record ContainerTooltipData(ContainerComponent contents) implements TooltipData {
}
