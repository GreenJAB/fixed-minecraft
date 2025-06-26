package net.greenjab.fixedminecraft.models;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;

/** Credit: Viola-Siemens */
public class VillagerArmorLayer<S extends LivingEntityRenderState & HumanoidRenderState, M extends EntityModel<S>, A extends EntityModel<S> & HumanoidModel> extends FeatureRenderer<S, M> {
    private final A innerModel;
    private final A outerModel;
    private final EquipmentRenderer equipmentRenderer;

    public VillagerArmorLayer(
            FeatureRendererContext<S, M> context, A innerModel, A outerModel, EquipmentRenderer equipmentRenderer
    ) {
        super(context);
        this.innerModel = innerModel;
        this.outerModel = outerModel;
        this.equipmentRenderer = equipmentRenderer;
    }

    private static boolean hasModel(EquippableComponent component, EquipmentSlot slot) {
        return component.assetId().isPresent() && component.slot() == slot;
    }

    public void render(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, S bipedEntityRenderState, float f, float g) {
        this.renderArmor(
                matrixStack,
                vertexConsumerProvider,
                bipedEntityRenderState.fixed$chestEquipment(),
                EquipmentSlot.CHEST,
                i,
                this.getModel(EquipmentSlot.CHEST)
        );
        this.renderArmor(
                matrixStack,
                vertexConsumerProvider,
                bipedEntityRenderState.fixed$legEquipment(),
                EquipmentSlot.LEGS,
                i,
                this.getModel(EquipmentSlot.LEGS)
        );
        this.renderArmor(
                matrixStack,
                vertexConsumerProvider,
                bipedEntityRenderState.fixed$feetEquipment(),
                EquipmentSlot.FEET,
                i,
                this.getModel(EquipmentSlot.FEET)
        );
        this.renderArmor(
                matrixStack,
                vertexConsumerProvider,
                bipedEntityRenderState.fixed$headEquipment(),
                EquipmentSlot.HEAD,
                i,
                this.getModel(EquipmentSlot.HEAD)
        );
    }


    private void renderArmor(MatrixStack matrices, VertexConsumerProvider vertexConsumers, ItemStack stack, EquipmentSlot slot, int light, A armorModel) {
        EquippableComponent equippableComponent = stack.get(DataComponentTypes.EQUIPPABLE);
        if (equippableComponent != null && hasModel(equippableComponent, slot)) {
            //this.getContextModel().copyTransforms(armorModel);
            armorModel.propertiesCopyFrom(this.getContextModel());
            this.setVisible(armorModel, slot);
            EquipmentModel.LayerType layerType = this.usesInnerModel(slot) ? EquipmentModel.LayerType.HUMANOID_LEGGINGS : EquipmentModel.LayerType.HUMANOID;
            this.equipmentRenderer
                    .render(layerType, equippableComponent.assetId().orElseThrow(), armorModel, stack, matrices, vertexConsumers, light);
        }
    }

    protected void setVisible(A bipedModel, EquipmentSlot slot) {
        bipedModel.setAllVisible(false);
        switch (slot) {
            case HEAD:
                bipedModel.setHeadVisible(true);
                bipedModel.setHatVisible(true);
                break;
            case CHEST:
                bipedModel.setBodyVisible(true);
                bipedModel.setArmsVisible(true);
                break;
            case LEGS:
                bipedModel.setBodyVisible(true);
                bipedModel.setLegsVisible(true);
                break;
            case FEET:
                bipedModel.setLegsVisible(true);
        }
    }

    private A getModel(EquipmentSlot slot) {
        return this.usesInnerModel(slot) ? this.innerModel : this.outerModel;
    }


    private boolean usesInnerModel(EquipmentSlot slotType) {
        return slotType == EquipmentSlot.LEGS;
    }

}
