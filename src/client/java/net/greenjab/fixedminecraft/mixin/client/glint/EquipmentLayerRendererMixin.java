package net.greenjab.fixedminecraft.mixin.client.glint;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.greenjab.fixedminecraft.render.EnchantGlint;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.layers.EquipmentLayerRenderer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.EquipmentAsset;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EquipmentLayerRenderer.class)
@Environment(EnvType.CLIENT)
public abstract class EquipmentLayerRendererMixin {

    @Unique private static final EquipmentClientInfo chainmailModel = createHumanoidAndHorseModel("chainmail");
    @Unique private static final EquipmentClientInfo copperExposedModel = createHumanoidAndHorseModel("copper_exposed");
    @Unique private static final EquipmentClientInfo copperWeatheredModel = createHumanoidAndHorseModel("copper_weathered");
    @Unique private static final EquipmentClientInfo copperOxidizedModel = createHumanoidAndHorseModel("copper_oxidized");
    @Unique private static final EquipmentClientInfo scuteNautilusArmor = EquipmentClientInfo.builder()
            .addLayers(EquipmentClientInfo.LayerType.NAUTILUS_BODY, EquipmentClientInfo.Layer.onlyIfDyed(Identifier.withDefaultNamespace("armadillo_scute"), false))
            .addLayers(EquipmentClientInfo.LayerType.NAUTILUS_BODY, EquipmentClientInfo.Layer.onlyIfDyed(Identifier.withDefaultNamespace("armadillo_scute_overlay"), true)).build();

    @ModifyExpressionValue(method = "renderLayers(Lnet/minecraft/client/resources/model/EquipmentClientInfo$LayerType;Lnet/minecraft/resources/ResourceKey;Lnet/minecraft/client/model/Model;Ljava/lang/Object;Lnet/minecraft/world/item/ItemStack;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;ILnet/minecraft/resources/Identifier;II)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/resources/model/EquipmentAssetManager;get(Lnet/minecraft/resources/ResourceKey;)Lnet/minecraft/client/resources/model/EquipmentClientInfo;"))
    private EquipmentClientInfo useNewArmorModel(EquipmentClientInfo original, @Local(argsOnly = true) ResourceKey<EquipmentAsset> equipmentAssetId,
                                                 @Local(argsOnly = true) ItemStack itemStack, @Local(argsOnly = true) EquipmentClientInfo.LayerType layerType) {
        if (equipmentAssetId.toString().toLowerCase().contains("chainmail")) return chainmailModel;
        if (equipmentAssetId.toString().toLowerCase().contains("armadillo_scute") && layerType == EquipmentClientInfo.LayerType.NAUTILUS_BODY) return scuteNautilusArmor;
        if (equipmentAssetId.toString().toLowerCase().contains("copper")) {
            float durability = itemStack.getDamageValue() / (itemStack.getMaxDamage() + 0.0f);
            if (durability>0.75f)  return copperOxidizedModel;
            if (durability>0.5f) return copperWeatheredModel;
            if (durability>0.25f) return copperExposedModel;
        }
        return original;
    }

    @WrapOperation(method = "renderLayers(Lnet/minecraft/client/resources/model/EquipmentClientInfo$LayerType;Lnet/minecraft/resources/ResourceKey;Lnet/minecraft/client/model/Model;Ljava/lang/Object;Lnet/minecraft/world/item/ItemStack;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;ILnet/minecraft/resources/Identifier;II)V", at =
    @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/rendertype/RenderTypes;trimmedArmorGlint()Lnet/minecraft/client/renderer/rendertype/RenderType;"))
    private RenderType getGlintTrident(Operation<RenderType> original) {
        return EnchantGlint.getArmorEntityGlint();
    }

    @Unique private static EquipmentClientInfo createHumanoidAndHorseModel(String id) {
        return EquipmentClientInfo.builder().addHumanoidLayers(Identifier.withDefaultNamespace(id))
                .addLayers(EquipmentClientInfo.LayerType.HORSE_BODY, EquipmentClientInfo.Layer.leatherDyeable(Identifier.withDefaultNamespace(id), false)).build();
    }

    @Inject(method = "renderLayers(Lnet/minecraft/client/resources/model/EquipmentClientInfo$LayerType;Lnet/minecraft/resources/ResourceKey;Lnet/minecraft/client/model/Model;Ljava/lang/Object;Lnet/minecraft/world/item/ItemStack;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;ILnet/minecraft/resources/Identifier;II)V", at = @At("HEAD"))
    private <S> void setEnchantGlintItemStack(EquipmentClientInfo.LayerType layerType, ResourceKey<EquipmentAsset> equipmentAssetId, Model<? super S> model,
                              S state, ItemStack itemStack, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int lightCoords,
                              @Nullable Identifier playerTextureOverride, int outlineColor, int order, CallbackInfo ci) {
        EnchantGlint.setTargetStack(itemStack);
    }
}
