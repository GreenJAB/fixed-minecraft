package net.greenjab.fixedminecraft.mixin.client.glint;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.greenjab.fixedminecraft.FixedMinecraftClient;
import net.greenjab.fixedminecraft.render.EnchantGlint;
import net.minecraft.client.renderer.entity.layers.EquipmentLayerRenderer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.EquipmentAsset;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EquipmentLayerRenderer.class)
@Environment(EnvType.CLIENT)
public abstract class EquipmentLayerRendererMixin {

    @ModifyExpressionValue(method = "renderLayers(Lnet/minecraft/client/resources/model/EquipmentClientInfo$LayerType;Lnet/minecraft/resources/ResourceKey;Lnet/minecraft/client/model/Model;Ljava/lang/Object;Lnet/minecraft/world/item/ItemStack;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;ILnet/minecraft/resources/Identifier;II)V", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/resources/model/EquipmentAssetManager;get(Lnet/minecraft/resources/ResourceKey;)Lnet/minecraft/client/resources/model/EquipmentClientInfo;"
    ))
    private EquipmentClientInfo useNewArmorModel(EquipmentClientInfo original,
                                                 @Local(argsOnly = true) ResourceKey<EquipmentAsset> equipmentAssetId,
                                                 @Local(argsOnly = true)
                                            ItemStack itemStack,
                                                 @Local(argsOnly = true) EquipmentClientInfo.LayerType layerType) {
        if (equipmentAssetId.toString().toLowerCase().contains("chainmail")) return FixedMinecraftClient.chainmailModel;
        if (equipmentAssetId.toString().toLowerCase().contains("armadillo_scute") && layerType == EquipmentClientInfo.LayerType.NAUTILUS_BODY) return FixedMinecraftClient.scuteNautilusArmor;
        if (equipmentAssetId.toString().toLowerCase().contains("copper") && (layerType == EquipmentClientInfo.LayerType.HUMANOID || layerType == EquipmentClientInfo.LayerType.HUMANOID_LEGGINGS)) {
            float durability = itemStack.getDamageValue() / (itemStack.getMaxDamage() + 0.0f);
            if (durability>0.75f) {
                return FixedMinecraftClient.copperOxidizedModel;
            }
            if (durability>0.5f) {
                return FixedMinecraftClient.copperWeatheredModel;
            }
            if (durability>0.25f) {
                return FixedMinecraftClient.copperExposedModel;
            }
        }
        return original;
    }

    @Redirect(method = "renderLayers(Lnet/minecraft/client/resources/model/EquipmentClientInfo$LayerType;Lnet/minecraft/resources/ResourceKey;Lnet/minecraft/client/model/Model;Ljava/lang/Object;Lnet/minecraft/world/item/ItemStack;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;ILnet/minecraft/resources/Identifier;II)V",
              at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/rendertype/RenderTypes;armorEntityGlint()Lnet/minecraft/client/renderer/rendertype/RenderType;"))
    private RenderType getGlintTrident() {
        return EnchantGlint.getArmorEntityGlint();
    }
}
