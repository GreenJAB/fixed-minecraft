package net.greenjab.fixedminecraft.mixin.client.map;

import net.greenjab.fixedminecraft.registry.item.map_book.MapBookItem;
import net.greenjab.fixedminecraft.registry.item.map_book.MapStateData;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/** Credit: Nettakrim */
@Mixin(HeldItemRenderer.class)
public class HeldItemRendererMixin {
    @Shadow
    @Final
    private MinecraftClient client;

    @ModifyVariable(at = @At(value = "HEAD"), method = "renderFirstPersonMap", argsOnly = true)
    private ItemStack sneakySwap(ItemStack original) {
        if (original.getItem() instanceof MapBookItem mapBookItem) {
            //pretend the map book is actually a filled map item, this ensures it renders properly, even when if offhand etc
            assert client.player !=null;
            MapStateData nearestMap = mapBookItem.getNearestMap(original, client.world, client.player.getEntityPos());
            if (nearestMap == null) return original;
            ItemStack map = new ItemStack(Items.FILLED_MAP, 1);
            map.set(DataComponentTypes.MAP_ID, nearestMap.id);
            return map;
        }
        return original;
    }
}
