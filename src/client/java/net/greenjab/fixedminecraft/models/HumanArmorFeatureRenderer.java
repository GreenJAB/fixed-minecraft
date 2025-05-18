package net.greenjab.fixedminecraft.models;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;

/** Credit: Viola-Siemens */
public class HumanArmorFeatureRenderer{}/*
public class HumanArmorFeatureRenderer<S extends BipedEntityRenderState, M extends BipedEntityModel<S>, A extends BipedEntityModel<S>> extends FeatureRenderer<S, M> {
    private final A innerModel;
    private final A outerModel;
    private final EquipmentRenderer equipmentRenderer;

    public HumanArmorFeatureRenderer(
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
                bipedEntityRenderState.equippedChestStack,
                EquipmentSlot.CHEST,
                i,
                this.getModel(EquipmentSlot.CHEST)
        );
        this.renderArmor(
                matrixStack,
                vertexConsumerProvider,
                bipedEntityRenderState.equippedLegsStack,
                EquipmentSlot.LEGS,
                i,
                this.getModel(EquipmentSlot.LEGS)
        );
        this.renderArmor(
                matrixStack,
                vertexConsumerProvider,
                bipedEntityRenderState.equippedFeetStack,
                EquipmentSlot.FEET,
                i,
                this.getModel(EquipmentSlot.FEET)
        );
        this.renderArmor(
                matrixStack,
                vertexConsumerProvider,
                bipedEntityRenderState.equippedHeadStack,
                EquipmentSlot.HEAD,
                i,
                this.getModel(EquipmentSlot.HEAD)
        );
    }


    private void renderArmor(MatrixStack matrices, VertexConsumerProvider vertexConsumers, ItemStack stack, EquipmentSlot slot, int light, A armorModel) {
        EquippableComponent equippableComponent = stack.get(DataComponentTypes.EQUIPPABLE);
        if (equippableComponent != null && hasModel(equippableComponent, slot)) {
            this.getContextModel().copyTransforms(armorModel);
            this.setVisible(armorModel, slot);
            EquipmentModel.LayerType layerType = this.usesInnerModel(slot) ? EquipmentModel.LayerType.HUMANOID_LEGGINGS : EquipmentModel.LayerType.HUMANOID;
            this.equipmentRenderer
                    .render(layerType, equippableComponent.assetId().orElseThrow(), armorModel, stack, matrices, vertexConsumers, light);
        }
    }

    protected void setVisible(A bipedModel, EquipmentSlot slot) {
        bipedModel.setVisible(false);
        switch (slot) {
            case HEAD:
                bipedModel.head.visible = true;
                bipedModel.hat.visible = true;
                break;
            case CHEST:
                bipedModel.body.visible = true;
                bipedModel.rightArm.visible = true;
                bipedModel.leftArm.visible = true;
                break;
            case LEGS:
                bipedModel.body.visible = true;
                bipedModel.rightLeg.visible = true;
                bipedModel.leftLeg.visible = true;
                break;
            case FEET:
                bipedModel.rightLeg.visible = true;
                bipedModel.leftLeg.visible = true;
        }
    }

    private A getModel(EquipmentSlot slot) {
        return this.usesInnerModel(slot) ? this.innerModel : this.outerModel;
    }


    private boolean usesInnerModel(EquipmentSlot slotType) {
        return slotType == EquipmentSlot.LEGS;
    }

}*/
