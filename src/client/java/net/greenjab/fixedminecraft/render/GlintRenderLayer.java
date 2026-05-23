package net.greenjab.fixedminecraft.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.rendertype.LayeringTransform;
import net.minecraft.client.renderer.rendertype.OutputTarget;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.TextureTransform;
import net.minecraft.resources.Identifier;

/** Credit: Pepperoni-Jabroni */
@Environment(EnvType.CLIENT)
public abstract class GlintRenderLayer extends RenderType {

    private static final Identifier item_texture = Identifier.parse("textures/misc/super_enchanted_glint_item.png");
    private static final Identifier entity_texture = Identifier.parse("textures/misc/super_enchanted_glint_entity.png");


    public static final RenderType glintColor = RenderType.create(
            "glint",
            RenderSetup.builder(RenderPipelines.GLINT)
                    .withTexture("Sampler0", item_texture)
                    .setTextureTransform(TextureTransform.GLINT_TEXTURING)
                    .createRenderSetup()
    );
    public static final RenderType entityGlintColor = RenderType.create(
            "entity_glint",
            RenderSetup.builder(RenderPipelines.GLINT)
                    .withTexture("Sampler0", item_texture)
                    .setTextureTransform(TextureTransform.ENTITY_GLINT_TEXTURING)
                    .createRenderSetup()
    );
    public static final RenderType armorEntityGlintColor = RenderType.create(
            "armor_entity_glint",
            RenderSetup.builder(RenderPipelines.GLINT)
                    .withTexture("Sampler0", entity_texture)
                    .setTextureTransform(TextureTransform.ARMOR_ENTITY_GLINT_TEXTURING)
                    .setLayeringTransform(LayeringTransform.VIEW_OFFSET_Z_LAYERING)
                    .createRenderSetup()
    );
    public static final RenderType translucentGlintColor = RenderType.create(
            "glint_translucent",
            RenderSetup.builder(RenderPipelines.GLINT)
                    .withTexture("Sampler0", item_texture)
                    .setTextureTransform(TextureTransform.GLINT_TEXTURING)
                    .setOutputTarget(OutputTarget.ITEM_ENTITY_TARGET)
                    .createRenderSetup()
    );

    public GlintRenderLayer(String name, RenderSetup renderSetup) {
        super(name, renderSetup);
    }



}
