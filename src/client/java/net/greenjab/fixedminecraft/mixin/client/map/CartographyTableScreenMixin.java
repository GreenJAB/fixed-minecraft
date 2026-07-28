package net.greenjab.fixedminecraft.mixin.client.map;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.greenjab.fixedminecraft.registry.item.map_book.MapBookItem;
import net.minecraft.client.gui.screens.inventory.CartographyTableScreen;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.saveddata.maps.MapId;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(CartographyTableScreen.class)
public abstract class CartographyTableScreenMixin {

    @WrapOperation(method = "extractBackground", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;get(Lnet/minecraft/core/component/DataComponentType;)Ljava/lang/Object;"))
    private Object mapBookDisplay(ItemStack instance, DataComponentType<MapId> dataComponentType, Operation<Object> original) {
        if (instance.getItem() instanceof MapBookItem) return null;
        return original.call(instance, dataComponentType);
    }
}
