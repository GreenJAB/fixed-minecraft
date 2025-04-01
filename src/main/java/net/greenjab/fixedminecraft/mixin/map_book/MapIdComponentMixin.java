package net.greenjab.fixedminecraft.mixin.map_book;

import net.greenjab.fixedminecraft.registry.registries.ItemRegistry;
import net.minecraft.component.ComponentsAccess;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.MapIdComponent;
import net.minecraft.item.Item;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

@Mixin(MapIdComponent.class)
public abstract class MapIdComponentMixin {

    @Inject(method = "appendTooltip", at = @At(value = "HEAD"), cancellable = true)
    private void mapBookNoTooltop(Item.TooltipContext context, Consumer<Text> textConsumer, TooltipType type, ComponentsAccess components,
                              CallbackInfo ci){
        if (components.getOrDefault(DataComponentTypes.REPAIR_COST, 0)==3) ci.cancel();
    }
}
