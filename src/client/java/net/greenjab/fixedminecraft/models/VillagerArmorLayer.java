package net.greenjab.fixedminecraft.models;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EquipmentLayerRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.Equippable;
import org.jspecify.annotations.NonNull;

/** Credit: Viola-Siemens */
public class VillagerArmorLayer<S extends LivingEntityRenderState & HumanoidRenderState, M extends EntityModel<@NonNull S>, A extends EntityModel<@NonNull S> & CustomHumanoidModel> extends RenderLayer<@NonNull S, M> {
    private final A Model_HEAD;
    private final A Model_CHEST;
    private final A Model_LEGS;
    private final A Model_FEET;
    private final EquipmentLayerRenderer equipmentRenderer;

    public VillagerArmorLayer(
            RenderLayerParent<@NonNull S, M> context, A Model_HEAD, A Model_CHEST, A Model_LEGS, A Model_FEET, EquipmentLayerRenderer equipmentRenderer
    ) {
        super(context);
        this.Model_HEAD = Model_HEAD;
        this.Model_CHEST = Model_CHEST;
        this.Model_LEGS = Model_LEGS;
        this.Model_FEET = Model_FEET;
        this.equipmentRenderer = equipmentRenderer;
    }

    private static boolean hasModel(Equippable component, EquipmentSlot slot) {
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
    public void submit(@NonNull PoseStack matrixStack, @NonNull SubmitNodeCollector entityRenderCommandQueue, int light, @NonNull S bipedEntityRenderState, float limbAngle, float limbDistance) {

        this.renderArmor(matrixStack, entityRenderCommandQueue, bipedEntityRenderState.fixed$chestEquipment(), EquipmentSlot.CHEST, light, bipedEntityRenderState);
        this.renderArmor(matrixStack, entityRenderCommandQueue, bipedEntityRenderState.fixed$legEquipment(), EquipmentSlot.LEGS, light, bipedEntityRenderState);
        this.renderArmor(matrixStack, entityRenderCommandQueue, bipedEntityRenderState.fixed$feetEquipment(), EquipmentSlot.FEET, light, bipedEntityRenderState);
        this.renderArmor(matrixStack, entityRenderCommandQueue, bipedEntityRenderState.fixed$headEquipment(), EquipmentSlot.HEAD, light, bipedEntityRenderState);

    }
    private void renderArmor(
            PoseStack matrices, SubmitNodeCollector entityRenderCommandQueue, ItemStack stack, EquipmentSlot slot, int light, S bipedEntityRenderState
    ) {
        Equippable equippableComponent = stack.get(DataComponents.EQUIPPABLE);
        if (equippableComponent != null && hasModel(equippableComponent, slot)) {
            A bipedEntityModel = getModel(slot);
            bipedEntityModel.propertiesCopyFrom(this.getParentModel());
            this.setVisible(bipedEntityModel, slot);
            EquipmentClientInfo.LayerType layerType = this.usesInnerModel(slot) ? EquipmentClientInfo.LayerType.HUMANOID_LEGGINGS : EquipmentClientInfo.LayerType.HUMANOID;
            this.equipmentRenderer
                    .renderLayers(
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
