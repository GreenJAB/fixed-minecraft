package net.greenjab.fixedminecraft.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.rendertype.LayeringTransform;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.TextureTransform;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;

import java.util.function.Function;

/** Credit: Pepperoni-Jabroni */
@Environment(EnvType.CLIENT)
public abstract class GlintRenderLayer extends RenderType {

    private static final Identifier item_texture = Identifier.parse("textures/misc/super_enchanted_glint_item.png");
    private static final Identifier entity_texture = Identifier.parse("textures/misc/super_enchanted_glint_entity.png");

    public static final RenderType armorEntityGlintColor = RenderType.create(
            "armor_entity_glint",
            RenderSetup.builder(RenderPipelines.GLINT)
                    .withTexture("Sampler0", entity_texture)
                    .setTextureTransform(TextureTransform.ARMOR_ENTITY_GLINT_TEXTURING)
                    .setLayeringTransform(LayeringTransform.VIEW_OFFSET_Z_LAYERING)
                    .createRenderSetup()
    );

    public static final Function<Identifier, RenderType> ENTITY_SOLID_GLINT = Util.memoize(
             texture -> {
                RenderSetup state = RenderSetup.builder(RenderPipelines.ENTITY_SOLID_GLINT)
                        .withTexture("Sampler0", texture)
                        .withTexture("GlintSampler", entity_texture)
                        .setTextureTransform(TextureTransform.ENTITY_GLINT_TEXTURING)
                        .useLightmap()
                        .useOverlay()
                        .affectsCrumbling()
                        .setOutline(RenderSetup.OutlineProperty.AFFECTS_OUTLINE)
                        .createRenderSetup();
                return RenderType.create("entity_solid_glint", state);
            }
    );

    public static final Function<Identifier, RenderType> ARMOR_CUTOUT_NO_CULL_GLINT = Util.memoize(
            texture -> {
                RenderSetup state = RenderSetup.builder(RenderPipelines.ARMOR_CUTOUT_NO_CULL_GLINT)
                        .withTexture("Sampler0", texture)
                        .withTexture("GlintSampler", entity_texture)
                        .setTextureTransform(TextureTransform.ARMOR_ENTITY_GLINT_TEXTURING)
                        .useLightmap()
                        .useOverlay()
                        .setLayeringTransform(LayeringTransform.VIEW_OFFSET_Z_LAYERING)
                        .affectsCrumbling()
                        .setOutline(RenderSetup.OutlineProperty.AFFECTS_OUTLINE)
                        .createRenderSetup();
                return RenderType.create("armor_cutout_no_cull_glint", state);
            }
    );

    public static final Function<Identifier, RenderType> ITEM_TRANSLUCENT_GLINT_SPECIAL = Util.memoize(
             texture -> {
                RenderSetup state = RenderSetup.builder(RenderPipelines.ITEM_TRANSLUCENT_GLINT_SPECIAL)
                        .setOitPipelines(RenderPipelines.OIT_ITEM_GLINT_SPECIAL)
                        .withTexture("Sampler0", texture)
                        .withTexture("GlintSampler", item_texture)
                        .setTextureTransform(TextureTransform.GLINT_TEXTURING)
                        .useLightmap()
                        .useOverlay()
                        .affectsCrumbling()
                        .sortOnUpload()
                        .setOutline(RenderSetup.OutlineProperty.AFFECTS_OUTLINE)
                        .createRenderSetup();
                return RenderType.create("item_translucent_glint_special", state);
            }
    );

    public static final Function<Identifier, RenderType> ITEM_CUTOUT_GLINT_SPECIAL = Util.memoize(
             texture -> {
                RenderSetup state = RenderSetup.builder(RenderPipelines.ITEM_CUTOUT_GLINT_SPECIAL)
                        .withTexture("Sampler0", texture)
                        .withTexture("GlintSampler", item_texture)
                        .setTextureTransform(TextureTransform.GLINT_TEXTURING)
                        .useLightmap()
                        .useOverlay()
                        .affectsCrumbling()
                        .setOutline(RenderSetup.OutlineProperty.AFFECTS_OUTLINE)
                        .createRenderSetup();
                return RenderType.create("item_cutout_glint_special", state);
            }
    );

    public GlintRenderLayer(String name, RenderSetup renderSetup) {
        super(name, renderSetup);
    }
}
