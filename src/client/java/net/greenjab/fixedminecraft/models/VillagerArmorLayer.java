package net.greenjab.fixedminecraft.models;

import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.equipment.EquipmentModel;
import net.minecraft.client.render.entity.equipment.EquipmentRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.EquippableComponent;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;

/** Credit: Viola-Siemens */
public class VillagerArmorLayer<S extends LivingEntityRenderState & HumanoidRenderState, M extends EntityModel<S>, A extends EntityModel<S> & HumanoidModel> extends FeatureRenderer<S, M> {
    //private final EquipmentModelData<A> Model;
    private final A Model_HEAD;
    private final A Model_CHEST;
    private final A Model_LEGS;
    private final A Model_FEET;
    private final EquipmentRenderer equipmentRenderer;

    public VillagerArmorLayer(
            FeatureRendererContext<S, M> context, A Model_HEAD,A Model_CHEST,A Model_LEGS,A Model_FEET, EquipmentRenderer equipmentRenderer
    ) {
        super(context);
        this.Model_HEAD = Model_HEAD;
        this.Model_CHEST = Model_CHEST;
        this.Model_LEGS = Model_LEGS;
        this.Model_FEET = Model_FEET;
        this.equipmentRenderer = equipmentRenderer;
    }

    private static boolean hasModel(EquippableComponent component, EquipmentSlot slot) {
        return component.assetId().isPresent() && component.slot() == slot;
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
        if (slot == EquipmentSlot.HEAD) {
            return Model_HEAD;
        } else if (slot == EquipmentSlot.CHEST) {
            return Model_CHEST;
        } else if (slot == EquipmentSlot.LEGS) {
            return Model_LEGS;
        } else {
            return Model_FEET;
        }
    }

    private boolean usesInnerModel(EquipmentSlot slotType) {
        return slotType == EquipmentSlot.LEGS;
    }

    @Override
    public void render(MatrixStack matrixStack, OrderedRenderCommandQueue entityRenderCommandQueue, int light, S bipedEntityRenderState, float limbAngle, float limbDistance) {

        this.renderArmor(matrixStack, entityRenderCommandQueue, bipedEntityRenderState.fixed$chestEquipment(), EquipmentSlot.CHEST, light, bipedEntityRenderState);
        this.renderArmor(matrixStack, entityRenderCommandQueue, bipedEntityRenderState.fixed$legEquipment(), EquipmentSlot.LEGS, light, bipedEntityRenderState);
        this.renderArmor(matrixStack, entityRenderCommandQueue, bipedEntityRenderState.fixed$feetEquipment(), EquipmentSlot.FEET, light, bipedEntityRenderState);
        this.renderArmor(matrixStack, entityRenderCommandQueue, bipedEntityRenderState.fixed$headEquipment(), EquipmentSlot.HEAD, light, bipedEntityRenderState);

    }
    private void renderArmor(
            MatrixStack matrices, OrderedRenderCommandQueue entityRenderCommandQueue, ItemStack stack, EquipmentSlot slot, int light, S bipedEntityRenderState
    ) {
        EquippableComponent equippableComponent = stack.get(DataComponentTypes.EQUIPPABLE);
        if (equippableComponent != null && hasModel(equippableComponent, slot)) {
            A bipedEntityModel = getModel(slot);
            bipedEntityModel.propertiesCopyFrom(this.getContextModel());
            this.setVisible(bipedEntityModel, slot);
            EquipmentModel.LayerType layerType = this.usesInnerModel(slot) ? EquipmentModel.LayerType.HUMANOID_LEGGINGS : EquipmentModel.LayerType.HUMANOID;
            this.equipmentRenderer
                    .render(
                            layerType,
                            equippableComponent.assetId().orElseThrow(),
                            bipedEntityModel,
                            bipedEntityRenderState,
                            stack,
                            matrices,
                            entityRenderCommandQueue,
                            light,
                            bipedEntityRenderState.outlineColor
                    );
        }
    }
}
