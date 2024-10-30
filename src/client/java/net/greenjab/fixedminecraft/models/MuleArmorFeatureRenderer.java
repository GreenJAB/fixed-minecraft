package net.greenjab.fixedminecraft.models;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.EntityModelLoader;
import net.minecraft.client.render.entity.model.HorseEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.passive.AbstractDonkeyEntity;
import net.minecraft.entity.passive.MuleEntity;
import net.minecraft.item.DyeableHorseArmorItem;
import net.minecraft.item.HorseArmorItem;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Unique;

@Environment(EnvType.CLIENT)
public class MuleArmorFeatureRenderer extends FeatureRenderer<MuleEntity, HorseEntityModel<MuleEntity>> {
    private final HorseEntityModel<MuleEntity> model;

    public MuleArmorFeatureRenderer(FeatureRendererContext<MuleEntity, HorseEntityModel<MuleEntity>> context, EntityModelLoader loader) {
        super(context);
        this.model = new HorseEntityModel<>(loader.getModelPart(EntityModelLayers.HORSE_ARMOR));
    }

    public void render(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, MuleEntity horseEntity, float f, float g, float h, float j, float k, float l) {
        ItemStack itemStack = getArmorType(horseEntity);
        if (itemStack.getItem() instanceof HorseArmorItem horseArmorItem) {
            this.getContextModel().copyStateTo(this.model);
            this.model.animateModel(horseEntity, f, g, h);
            this.model.setAngles(horseEntity, f, g, j, k, l);
            float n;
            float o;
            float p;
            if (horseArmorItem instanceof DyeableHorseArmorItem) {
                int m = ((DyeableHorseArmorItem)horseArmorItem).getColor(itemStack);
                n = (float)(m >> 16 & 255) / 255.0F;
                o = (float)(m >> 8 & 255) / 255.0F;
                p = (float)(m & 255) / 255.0F;
            } else {
                n = 1.0F;
                o = 1.0F;
                p = 1.0F;
            }

            VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(RenderLayer.getEntityCutoutNoCull(horseArmorItem.getEntityTexture()));
            this.model.render(matrixStack, vertexConsumer, i, OverlayTexture.DEFAULT_UV, n, o, p, 1.0F);
        }
    }
    @Unique
    public ItemStack getArmorType(AbstractDonkeyEntity horseEntity) {
        return horseEntity.getEquippedStack(EquipmentSlot.FEET);
    }
}
