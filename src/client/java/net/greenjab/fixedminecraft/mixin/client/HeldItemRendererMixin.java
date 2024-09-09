package net.greenjab.fixedminecraft.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.greenjab.fixedminecraft.registry.item.map_book.MapBookItem;
import net.greenjab.fixedminecraft.registry.item.map_book.MapStateData;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(HeldItemRenderer.class)
public class HeldItemRendererMixin {
    @Shadow @Final private MinecraftClient client;

    @Unique
    private MapStateData nearestMap;

    @WrapOperation(at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isOf(Lnet/minecraft/item/Item;)Z"), method = "renderFirstPersonItem")
    private boolean passMapCheck(ItemStack instance, Item item, Operation<Boolean> original) {
        if (original.call(instance, item)) return true;
        if (client.player == null || client.world == null) return false;

        if (!(instance.getItem() instanceof MapBookItem mapBookItem)) return false;
        nearestMap = mapBookItem.getNearestMap(instance, client.world, client.player.getPos());
        return nearestMap != null;
    }

    @ModifyVariable(at = @At(value = "HEAD"), method = "renderFirstPersonMap", argsOnly = true)
    private ItemStack sneakySwap(ItemStack original) {
        //pretend the map book is actually a filled map item, this ensures it renders properly, even when if offhand etc
        if (!(original.getItem() instanceof MapBookItem) || nearestMap == null) return original;
        ItemStack map = new ItemStack(Items.FILLED_MAP, 1);
        map.getOrCreateNbt().putInt("map", nearestMap.getId());
        return map;
    }
}
