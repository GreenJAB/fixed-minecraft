package net.greenjab.fixedminecraft.mixin.client.map;

import net.minecraft.client.render.entity.ItemFrameEntityRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(ItemFrameEntityRenderer.class)
public class ItemFrameEntityRendererMixin {
    @ModifyArg(method = "render(Lnet/minecraft/entity/decoration/ItemFrameEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
               at = @At(value = "INVOKE",
                        target = "Lnet/minecraft/client/render/MapRenderer;draw(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/item/map/MapState;ZI)V"
               ), index = 4)
    private boolean showIconsOnItemFrameMap(boolean hidePlayerIcons){
        return false;
    }
}
