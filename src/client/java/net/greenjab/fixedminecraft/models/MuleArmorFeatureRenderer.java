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
import net.minecraft.component.type.DyedColorComponent;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.passive.AbstractDonkeyEntity;
import net.minecraft.entity.passive.MuleEntity;
import net.minecraft.item.AnimalArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.util.math.ColorHelper;
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
        if (itemStack.getItem() instanceof AnimalArmorItem animalArmorItem &&
            animalArmorItem.getType() == AnimalArmorItem.Type.EQUESTRIAN) {
            this.getContextModel().copyStateTo(this.model);
            this.model.animateModel(horseEntity, f, g, h);
            this.model.setAngles(horseEntity, f, g, j, k, l);
            int m;
            if (itemStack.isIn(ItemTags.DYEABLE)) {
                m = ColorHelper.Argb.fullAlpha(DyedColorComponent.getColor(itemStack, -6265536));
            } else {
                m = -1;
            }

            VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(RenderLayer.getEntityCutoutNoCull(animalArmorItem.getEntityTexture()));
            this.model.render(matrixStack, vertexConsumer, i, OverlayTexture.DEFAULT_UV, m);
        }
    }
    @Unique
    public ItemStack getArmorType(AbstractDonkeyEntity horseEntity) {
        return horseEntity.getEquippedStack(EquipmentSlot.BODY);
    }
}
