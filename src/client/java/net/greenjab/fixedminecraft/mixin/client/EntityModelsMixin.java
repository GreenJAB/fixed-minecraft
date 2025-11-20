package net.greenjab.fixedminecraft.mixin.client;

import com.google.common.collect.ImmutableMap;
import com.llamalad7.mixinextras.sugar.Local;
import net.greenjab.fixedminecraft.registrties.EntityModelLayerRegistry;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.EntityModels;
import net.minecraft.client.render.entity.model.MinecartEntityModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(EntityModels.class)
public class EntityModelsMixin {

    @Inject(method = "getModels", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/block/entity/SignBlockEntityRenderer;getTexturedModelData(Z)Lnet/minecraft/client/model/TexturedModelData;", ordinal = 0))
    private static void addClamModel(CallbackInfoReturnable<Map<EntityModelLayer, TexturedModelData>> cir, @Local ImmutableMap.Builder<EntityModelLayer, TexturedModelData> builder) {
        builder.put(EntityModelLayerRegistry.DISPENSER_MINECART, MinecartEntityModel.getTexturedModelData());
    }
}
