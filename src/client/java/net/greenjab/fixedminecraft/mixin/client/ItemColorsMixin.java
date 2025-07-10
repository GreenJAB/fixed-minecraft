package net.greenjab.fixedminecraft.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.component.type.DyedColorComponent;
import net.minecraft.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemColors.class)
public class ItemColorsMixin {

    @Inject(method = "create", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/color/item/ItemColors;register(Lnet/minecraft/client/color/item/ItemColorProvider;[Lnet/minecraft/item/ItemConvertible;)V", ordinal = 0
    ))
    private static void compassColor(BlockColors blockColors, CallbackInfoReturnable<ItemColors> cir, @Local ItemColors itemColors){
        itemColors.register((stack, tintIndex) -> tintIndex != 1 ? -1 : DyedColorComponent.getColor(stack, -65535), Items.COMPASS);
    }
}
