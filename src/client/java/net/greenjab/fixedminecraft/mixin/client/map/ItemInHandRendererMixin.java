package net.greenjab.fixedminecraft.mixin.client.map;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
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
    @Shadow @Final private Minecraft minecraft;
    @Shadow private ItemStack mainHandItem;
    @Shadow private float mainHandHeight;
    @Shadow private float offHandHeight;
    @Shadow private ItemStack offHandItem;

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

    @WrapOperation(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Mth;clamp(FFF)F", ordinal = 0))
    private float showMapInBoatMain(float value, float min, float max, Operation<Float> original) {
        if (this.mainHandItem.getComponents().has(DataComponents.MAP_ID)) {
            float attackAnim = this.minecraft.player.getItemSwapScale(1.0F);
            float mainHandTargetHeight = this.mainHandItem != this.minecraft.player.getMainHandItem() ? 0.0F : attackAnim * attackAnim * attackAnim;
            return this.mainHandHeight + original.call(mainHandTargetHeight - this.mainHandHeight, -0.4F, 0.4F);
        }
        return original.call(value, min, max);
    }
    @WrapOperation(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Mth;clamp(FFF)F", ordinal = 1))
    private float showMapInBoatOff(float value, float min, float max, Operation<Float> original) {
        if (this.offHandItem.getComponents().has(DataComponents.MAP_ID)) {
            float offHandTargetHeight = this.offHandItem != this.minecraft.player.getOffhandItem() ? 0.0F : 1.0F;
            return this.offHandHeight + original.call(offHandTargetHeight - this.offHandHeight, -0.4F, 0.4F);
        }
        return original.call(value, min, max);
    }
}
