package net.greenjab.fixedminecraft.mixin.client.map;

import net.greenjab.fixedminecraft.registry.item.map_book.MapBookItem;
import net.greenjab.fixedminecraft.registry.item.map_book.MapStateData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/** Credit: Nettakrim */
@Mixin(ItemInHandRenderer.class)
public abstract class ItemInHandRendererMixin {
    @Shadow
    @Final
    private Minecraft minecraft;

    @ModifyVariable(at = @At(value = "HEAD"), method = "renderMap", argsOnly = true)
    private ItemStack sneakySwap(ItemStack itemStack) {
        if (itemStack.getItem() instanceof MapBookItem mapBookItem) {
            //pretend the map book is actually a filled map item, this ensures it renders properly, even when if offhand etc
            assert minecraft.player != null;
            MapStateData nearestMap = mapBookItem.getNearestMap(itemStack, minecraft.level, minecraft.player.position());
            if (nearestMap == null) return itemStack;
            ItemStack map = new ItemStack(Items.FILLED_MAP, 1);
            map.set(DataComponents.MAP_ID, nearestMap.id);
            return map;
        }
        return itemStack;
    }
}
