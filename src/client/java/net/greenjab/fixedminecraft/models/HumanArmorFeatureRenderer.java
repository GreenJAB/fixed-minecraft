package net.greenjab.fixedminecraft.models;

import com.google.common.collect.Maps;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.DyeableArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.trim.ArmorTrim;
import net.minecraft.item.trim.ArmorTrimPattern;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class HumanArmorFeatureRenderer<T extends LivingEntity, M extends EntityModel<T>, A extends EntityModel<T>> extends FeatureRenderer<T, M> {
    private static final Map<String, Identifier> ARMOR_TEXTURE_CACHE = Maps.newHashMap();
    private final A innerModel;
    private final A outerModel;
    private final SpriteAtlasTexture armorTrimAtlas;

    public HumanArmorFeatureRenderer(FeatureRendererContext<T, M> renderer, A innerModel, A outerModel, BakedModelManager modelManager) {
        super(renderer);
        this.innerModel = innerModel;
        this.outerModel = outerModel;
        this.armorTrimAtlas = modelManager.getAtlas(TexturedRenderLayers.ARMOR_TRIMS_ATLAS_TEXTURE);
    }

    @Override
    public void render(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, T livingEntity, float f, float g, float h, float j, float k, float l) {
        this.renderArmor(matrixStack, vertexConsumerProvider, livingEntity, EquipmentSlot.CHEST, i, this.getModel(EquipmentSlot.CHEST));
        this.renderArmor(matrixStack, vertexConsumerProvider, livingEntity, EquipmentSlot.LEGS, i, this.getModel(EquipmentSlot.LEGS));
        this.renderArmor(matrixStack, vertexConsumerProvider, livingEntity, EquipmentSlot.FEET, i, this.getModel(EquipmentSlot.FEET));
        this.renderArmor(matrixStack, vertexConsumerProvider, livingEntity, EquipmentSlot.HEAD, i, this.getModel(EquipmentSlot.HEAD));

    }

    private void renderArmor(MatrixStack matrices, VertexConsumerProvider vertexConsumers, T entity, EquipmentSlot armorSlot, int light, A model) {
        ItemStack itemStack = entity.getEquippedStack(armorSlot);
        Item var9 = itemStack.getItem();
        if (var9 instanceof ArmorItem armorItem) {
            if (armorItem.getSlotType() == armorSlot) {
                ((VillagerArmorModel<?>)model).propertiesCopyFrom(this.getContextModel());
                this.setPartVisibility(model, armorSlot);
                boolean bl = this.usesInnerModel(armorSlot);
                if (armorItem instanceof DyeableArmorItem dyeableArmorItem) {
                    int i = dyeableArmorItem.getColor(itemStack);
                    float f = (float)(i >> 16 & 255) / 255.0F;
                    float g = (float)(i >> 8 & 255) / 255.0F;
                    float h = (float)(i & 255) / 255.0F;
                    this.renderArmorParts(matrices, vertexConsumers, light, armorItem, model, bl, f, g, h, (String)null);
                    this.renderArmorParts(matrices, vertexConsumers, light, armorItem, model, bl, 1.0F, 1.0F, 1.0F, "overlay");
                } else {
                    this.renderArmorParts(matrices, vertexConsumers, light, armorItem, model, bl, 1.0F, 1.0F, 1.0F, (String)null);
                }

                ArmorTrim.getTrim(entity.getWorld().getRegistryManager(), itemStack, true).ifPresent((trim) -> {
                    this.renderTrim(armorItem.getMaterial(), matrices, vertexConsumers, light, trim, model, bl);
                });
                if (itemStack.hasGlint()) {
                    this.renderGlint(matrices, vertexConsumers, light, model);
                }

            }
        }
    }

    protected void setPartVisibility(A model, EquipmentSlot slotType) {
        ((VillagerArmorModel<?>)model).setAllVisible(false);
        switch (slotType) {
            case HEAD -> {
                ((VillagerArmorModel<?>)model).setHeadVisible(true);
                ((VillagerArmorModel<?>)model).setHatVisible(true);
            }
            case CHEST -> {
                ((VillagerArmorModel<?>)model).setBodyVisible(true);
                ((VillagerArmorModel<?>)model).setArmsVisible(true);
            }
            case LEGS -> {
                ((VillagerArmorModel<?>)model).setBodyVisible(true);
                ((VillagerArmorModel<?>)model).setLegsVisible(true);
            }
            case FEET -> ((VillagerArmorModel<?>)model).setLegsVisible(true);
        }
    }

    @SuppressWarnings("unused")
    private void renderArmorParts(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, ArmorItem item, A model, boolean secondTextureLayer, float red, float green, float blue, @Nullable String overlay) {
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getArmorCutoutNoCull(this.getArmorTexture(item, secondTextureLayer, overlay)));
        model.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV, red, green, blue, 1.0F);
    }

    private void renderTrim(ArmorMaterial material, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, ArmorTrim trim, A model, boolean leggings) {
        Sprite sprite = this.armorTrimAtlas.getSprite(leggings ? trim.getLeggingsModelId(material) : trim.getGenericModelId(material));
        VertexConsumer vertexConsumer = sprite.getTextureSpecificVertexConsumer(vertexConsumers.getBuffer(TexturedRenderLayers.getArmorTrims(((ArmorTrimPattern)trim.getPattern().value()).decal())));
        model.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 1.0F);
    }

    private void renderGlint(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, A model) {
        model.render(matrices, vertexConsumers.getBuffer(RenderLayer.getArmorEntityGlint()), light, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 1.0F);
    }

    private A getModel(EquipmentSlot slot) {
        return this.usesInnerModel(slot) ? this.innerModel : this.outerModel;
    }

    private Identifier getArmorTexture(ArmorItem item, boolean secondLayer, @Nullable String overlay) {
        String var10000 = item.getMaterial().getName();
        String string = "textures/models/armor/" + var10000 + "_layer_" + (secondLayer ? 2 : 1) + (overlay == null ? "" : "_" + overlay) + ".png";
        return (Identifier)ARMOR_TEXTURE_CACHE.computeIfAbsent(string, Identifier::new);
    }

    private boolean usesInnerModel(EquipmentSlot slotType) {
        return slotType == EquipmentSlot.LEGS;
    }
}
