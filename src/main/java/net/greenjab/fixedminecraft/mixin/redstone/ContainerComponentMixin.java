package net.greenjab.fixedminecraft.mixin.redstone;

import net.minecraft.component.ComponentsAccess;
import net.minecraft.component.type.ContainerComponent;
import net.minecraft.item.Item;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

@Mixin(ContainerComponent.class)
public class ContainerComponentMixin {

    @Inject(method = "appendTooltip", at = @At("HEAD"), cancellable = true)
    private void test(Item.TooltipContext context, Consumer<Text> textConsumer, TooltipType type, ComponentsAccess components, CallbackInfo ci){
        ci.cancel();
    }
}
