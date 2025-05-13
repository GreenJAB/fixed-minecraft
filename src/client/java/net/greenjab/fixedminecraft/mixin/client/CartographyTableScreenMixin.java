package net.greenjab.fixedminecraft.mixin.client;

import net.greenjab.fixedminecraft.registry.item.map_book.MapBookItem;
import net.minecraft.client.gui.screen.ingame.CartographyTableScreen;
import net.minecraft.component.ComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(CartographyTableScreen.class)
public class CartographyTableScreenMixin {

    @Redirect(method = "drawBackground", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;get(Lnet/minecraft/component/ComponentType;)Ljava/lang/Object;"))
    private Object mapBookDisplay(ItemStack itemStack, ComponentType componentType) {
        if (itemStack.getItem() instanceof MapBookItem) {
            return null;
        }
        return itemStack.get(DataComponentTypes.MAP_ID);
    }
}
