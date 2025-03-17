package net.greenjab.fixedminecraft.mixin.client.map;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.greenjab.fixedminecraft.registry.item.map_book.MapBookItem;
import net.greenjab.fixedminecraft.registry.item.map_book.MapStateData;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.component.ComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.MapIdComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(HeldItemRenderer.class)
public class HeldItemRendererMixin {
    @Shadow
    @Final
    private MinecraftClient client;

    @Unique
    private MapStateData nearestMap;

    @WrapOperation(at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;contains(Lnet/minecraft/component/ComponentType;)Z"), method = "renderFirstPersonItem")
    private boolean passMapCheck(ItemStack instance, ComponentType<MapIdComponent> componentType, Operation<Boolean> original) {
        if (!(instance.getItem() instanceof MapBookItem mapBookItem)) return original.call(instance, componentType);

        if (client.player == null || client.world == null) return false;
        nearestMap = mapBookItem.getNearestMap(instance, client.world, client.player.getPos());
        return nearestMap != null;
    }

    @ModifyVariable(at = @At(value = "HEAD"), method = "renderFirstPersonMap", argsOnly = true)
    private ItemStack sneakySwap(ItemStack original) {
        //pretend the map book is actually a filled map item, this ensures it renders properly, even when if offhand etc
        if (!(original.getItem() instanceof MapBookItem) || nearestMap == null) return original;
        ItemStack map = new ItemStack(Items.FILLED_MAP, 1);
        map.set(DataComponentTypes.MAP_ID, nearestMap.id);
        return map;
    }

    @ModifyArg(method = "renderFirstPersonMap",
               at = @At(value = "INVOKE",
              target = "Lnet/minecraft/client/render/MapRenderer;draw(Lnet/minecraft/client/render/MapRenderState;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ZI)V"
    ), index = 3)
    private boolean showIconsOnItemFrameMap(boolean bl){
        return false;
    }
}
