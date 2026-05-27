package net.greenjab.fixedminecraft.mixin.other;

import net.greenjab.fixedminecraft.registry.registries.TrimMaterialsRegistry;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.equipment.trim.MaterialAssetGroup;
import net.minecraft.world.item.equipment.trim.TrimMaterial;
import net.minecraft.world.item.equipment.trim.TrimMaterials;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TrimMaterials.class)
public abstract class TrimMaterialsMixin {

    @Shadow
    private static void register(BootstrapContext<TrimMaterial> context, ResourceKey<TrimMaterial> registryKey, Style hoverTextStyle,
                                 MaterialAssetGroup assets) {
        throw new UnsupportedOperationException("Implemented via mixin");
    }

    @Inject(method = "bootstrap", at = @At(value = "TAIL"))
    private static void addCoalTrim(BootstrapContext<TrimMaterial> context, CallbackInfo ci) {
        register(context, TrimMaterialsRegistry.COAL, Style.EMPTY.withColor(3947580), TrimMaterialsRegistry.COAL_ASSET);
    }
}
