package net.greenjab.fixedminecraft.mixin.redstone;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemContainerContents;

@Mixin(ItemContainerContents.class)
public abstract class ItemContainerContentsMixin {

    @Inject(method = "addToTooltip", at = @At("HEAD"), cancellable = true)
    private void removeTextFromItemContainers(Item.TooltipContext context, Consumer<Component> consumer, TooltipFlag flag, DataComponentGetter components, CallbackInfo ci){
        ci.cancel();
    }
}
