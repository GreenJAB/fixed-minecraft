package net.greenjab.fixedminecraft.mixin.map_book;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.saveddata.maps.MapId;

@Mixin(MapId.class)
public abstract class MapIdMixin {

    @Inject(method = "addToTooltip", at = @At(value = "HEAD"), cancellable = true)
    private void mapBookNoTooltop(Item.TooltipContext context, Consumer<Component> consumer, TooltipFlag flag, DataComponentGetter components,
                                  CallbackInfo ci){
        if (components.getOrDefault(DataComponents.REPAIR_COST, 0)==3) ci.cancel();
    }
}
