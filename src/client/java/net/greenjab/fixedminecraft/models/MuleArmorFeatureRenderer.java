package net.greenjab.fixedminecraft.models;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.equipment.EquipmentModel;
import net.minecraft.client.render.entity.equipment.EquipmentRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.DonkeyEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.HorseEntityModel;
import net.minecraft.client.render.entity.model.LoadedEntityModels;
import net.minecraft.client.render.entity.state.DonkeyEntityRenderState;
import net.minecraft.client.render.entity.state.HorseEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.EquippableComponent;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.passive.AbstractDonkeyEntity;
import net.minecraft.entity.passive.MuleEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.equipment.EquipmentAsset;
import net.minecraft.registry.RegistryKey;
import org.spongepowered.asm.mixin.Unique;

@Environment(EnvType.CLIENT)
public class MuleArmorFeatureRenderer extends FeatureRenderer<DonkeyEntityRenderState, DonkeyEntityModel> {
    private final DonkeyEntityModel model;
    private final DonkeyEntityModel babyModel;
    private final EquipmentRenderer equipmentRenderer;

    public MuleArmorFeatureRenderer(
            FeatureRendererContext<DonkeyEntityRenderState, DonkeyEntityModel> context, LoadedEntityModels loader, EquipmentRenderer equipmentRenderer) {
            //FeatureRendererContext<MuleEntity, HorseEntityModel<MuleEntity>> context, EntityModelLoader loader) {
        super(context);
        this.equipmentRenderer = equipmentRenderer;
        this.model = new DonkeyEntityModel(loader.getModelPart(EntityModelLayers.HORSE_ARMOR));
        this.babyModel = new DonkeyEntityModel(loader.getModelPart(EntityModelLayers.HORSE_ARMOR_BABY));
    }


    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, DonkeyEntityRenderState state,
                       float limbAngle, float limbDistance) {
        ItemStack itemStack = Items.DIAMOND_HORSE_ARMOR.getDefaultStack();// state.;
        EquippableComponent equippableComponent = (EquippableComponent)itemStack.get(DataComponentTypes.EQUIPPABLE);
        if (equippableComponent != null && !equippableComponent.assetId().isEmpty()) {
            DonkeyEntityModel donkeyEntityModel = state.baby ? this.babyModel : this.model;
            donkeyEntityModel.setAngles(state);
            this.equipmentRenderer
                    .render(
                            EquipmentModel.LayerType.HORSE_BODY,
                            (RegistryKey<EquipmentAsset>)equippableComponent.assetId().get(),
                            donkeyEntityModel,
                            itemStack,
                            matrices,
                            vertexConsumers,
                            light
                    );
        }
    }
}
