package net.greenjab.fixedminecraft.mixin.client;

import com.google.common.collect.ImmutableMap;
import com.llamalad7.mixinextras.sugar.Local;
import net.greenjab.fixedminecraft.registries.CustomEntityModelLayerRegistry;
import net.minecraft.client.model.geom.LayerDefinitions;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.object.cart.MinecartModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(LayerDefinitions.class)
public abstract class LayerDefinitionsMixin {

    @Inject(method = "createRoots", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/blockentity/StandingSignRenderer;createSignLayer(Z)Lnet/minecraft/client/model/geom/builders/LayerDefinition;", ordinal = 0))
    private static void addDispenserMinecartModel(CallbackInfoReturnable<Map<ModelLayerLocation, LayerDefinition>> cir,
                                                  @Local ImmutableMap.Builder<ModelLayerLocation, LayerDefinition> result) {
        result.put(CustomEntityModelLayerRegistry.DISPENSER_MINECART, MinecartModel.createBodyLayer());
    }
}
