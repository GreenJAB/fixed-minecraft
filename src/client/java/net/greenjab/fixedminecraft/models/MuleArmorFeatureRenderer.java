package net.greenjab.fixedminecraft.models;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.greenjab.fixedminecraft.registry.ItemRegistry;
import net.greenjab.fixedminecraft.render.EnchantGlint;
import net.minecraft.client.model.ModelPart;
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
import net.minecraft.component.type.DyedColorComponent;
import net.minecraft.component.type.EquippableComponent;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.passive.AbstractDonkeyEntity;
import net.minecraft.entity.passive.MuleEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.equipment.EquipmentAsset;
import net.minecraft.registry.RegistryKey;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
public class MuleArmorFeatureRenderer extends FeatureRenderer<DonkeyEntityRenderState, DonkeyEntityModel> {
    private final DonkeyEntityModel model;
    private final DonkeyEntityModel babyModel;
    private final EquipmentRenderer equipmentRenderer;

    public MuleArmorFeatureRenderer(
            FeatureRendererContext<DonkeyEntityRenderState, DonkeyEntityModel> context, LoadedEntityModels loader, EquipmentRenderer equipmentRenderer
    ) {

        super(context);
        this.equipmentRenderer = equipmentRenderer;
        this.model = new DonkeyEntityModel(loader.getModelPart(EntityModelLayers.MULE));
        this.babyModel = new DonkeyEntityModel(loader.getModelPart(EntityModelLayers.MULE_BABY));
    }
    @Override
    public void render(
            MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, DonkeyEntityRenderState donkeyEntityRenderState, float f, float g
    ) {
        ItemStack itemStack = getArmorData(donkeyEntityRenderState);// state.;
        EnchantGlint.setTargetStack(itemStack);
        EquippableComponent equippableComponent = (EquippableComponent)itemStack.get(DataComponentTypes.EQUIPPABLE);
        if (equippableComponent != null && !equippableComponent.assetId().isEmpty()) {
            DonkeyEntityModel donkeyEntityModel = donkeyEntityRenderState.baby ? this.babyModel : this.model;
            donkeyEntityModel.setAngles(donkeyEntityRenderState);
            this.equipmentRenderer
                    .render(
                            EquipmentModel.LayerType.HORSE_BODY,
                            (RegistryKey<EquipmentAsset>)equippableComponent.assetId().get(),
                            donkeyEntityModel,
                            itemStack,
                            matrixStack,
                            vertexConsumerProvider,
                            i
                    );
        }
    }

    private ItemStack getArmorData(DonkeyEntityRenderState donkeyEntityRenderState) {
        float data = donkeyEntityRenderState.headItemAnimationProgress;
        ItemStack armor = getArmorValue(((int)(data))%10).getDefaultStack();
        if (data>10) {
            armor.set(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true);
        }
        if (data>20) {
            armor.set(DataComponentTypes.REPAIR_COST, 1);
        }
        if (data-(int)data!=0) {
            armor.set(DataComponentTypes.DYED_COLOR, new DyedColorComponent((int)((data - (int)data) * 100000000), true) );
        }
        return armor;
    }

    Item getArmorValue(int i) {
        if (i==1) return Items.LEATHER_HORSE_ARMOR;
        if (i==2) return Items.IRON_HORSE_ARMOR;
        if (i==3) return Items.GOLDEN_HORSE_ARMOR;
        if (i==4) return Items.DIAMOND_HORSE_ARMOR;
        if (i==5) return ItemRegistry.NETHERITE_HORSE_ARMOR;
        return Items.AIR;
    }
}
