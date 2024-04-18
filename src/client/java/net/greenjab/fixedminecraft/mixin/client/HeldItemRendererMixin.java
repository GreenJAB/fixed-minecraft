package net.greenjab.fixedminecraft.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.greenjab.fixedminecraft.items.map_book.MapBookItem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(HeldItemRenderer.class)
public class HeldItemRendererMixin {
    @Shadow @Final private MinecraftClient client;

    @WrapOperation(at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isOf(Lnet/minecraft/item/Item;)Z"), method = "renderFirstPersonItem")
    private boolean passMapCheck(ItemStack instance, Item item, Operation<Boolean> original) {
        return original.call(instance, item) || instance.getItem() instanceof MapBookItem;
    }

    @ModifyVariable(at = @At(value = "HEAD"), method = "renderFirstPersonMap", argsOnly = true)
    private ItemStack sneakySwap(ItemStack original) {
        if (!(original.getItem() instanceof MapBookItem)) return original;
        ItemStack map = new ItemStack(Items.FILLED_MAP, 1);
        map.getOrCreateNbt().putInt("map", ((MapBookItem)original.getItem()).getNearestMap(original, client.world, client.player));
        return map;
    }
}
