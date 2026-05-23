package net.greenjab.fixedminecraft.mixin.client.map;

import net.greenjab.fixedminecraft.registry.item.map_book.MapBookItem;
import net.minecraft.client.gui.screens.inventory.CartographyTableScreen;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(CartographyTableScreen.class)
public abstract class CartographyTableScreenMixin {

    @Redirect(method = "extractBackground", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;get(Lnet/minecraft/core/component/DataComponentType;)Ljava/lang/Object;"))
    private Object mapBookDisplay(ItemStack instance, DataComponentType<?> dataComponentType) {
        if (instance.getItem() instanceof MapBookItem) {
            return null;
        }
        return instance.get(DataComponents.MAP_ID);
    }
}
