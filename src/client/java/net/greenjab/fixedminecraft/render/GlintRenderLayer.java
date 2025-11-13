package net.greenjab.fixedminecraft.render;

import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.render.LayeringTransform;
import net.minecraft.client.render.OutputTarget;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderSetup;
import net.minecraft.client.render.TextureTransform;
import net.minecraft.client.util.BufferAllocator;
import net.minecraft.util.Identifier;

/** Credit: Pepperoni-Jabroni */
@Environment(EnvType.CLIENT)
public abstract class GlintRenderLayer extends RenderLayer{

    public static RenderLayer glintColor = buildGlintRenderLayer();
    public static RenderLayer entityGlintColor = buildEntityGlintRenderLayer();
    public static RenderLayer armorEntityGlintColor = buildArmorEntityGlintRenderLayer();
    public static RenderLayer translucentGlintColor = buildTranslucentGlint();

    public GlintRenderLayer(String name, RenderSetup renderSetup) {
        super(name, renderSetup);
    }

    public static void addGlintTypes(Object2ObjectLinkedOpenHashMap<RenderLayer, BufferAllocator> map) {
        addGlintTypes(map, glintColor);
        addGlintTypes(map, entityGlintColor);
        addGlintTypes(map, armorEntityGlintColor);
        addGlintTypes(map, translucentGlintColor);
    }

    public static void addGlintTypes(Object2ObjectLinkedOpenHashMap<RenderLayer, BufferAllocator> map, RenderLayer renderType) {
            if (!map.containsKey(renderType))
                map.put(renderType, new BufferAllocator(renderType.getExpectedBufferSize()));
    }

    private static RenderLayer buildGlintRenderLayer() {
        final Identifier res = Identifier.of("textures/misc/super_enchanted_glint_item.png");

        return RenderLayer.of(
                "glint",
                RenderSetup.builder(RenderPipelines.GLINT)
                        .texture("Sampler0", res)
                        .textureTransform(TextureTransform.GLINT_TEXTURING)
                        .build()
        );
    }

    private static RenderLayer buildEntityGlintRenderLayer() {
        final Identifier res = Identifier.of( "textures/misc/super_enchanted_glint_entity.png");

        return RenderLayer.of(
                "entity_glint",
                RenderSetup.builder(RenderPipelines.GLINT)
                        .texture("Sampler0", res)
                        .textureTransform(TextureTransform.ENTITY_GLINT_TEXTURING)
                        .build()
        );
    }

    private static RenderLayer buildArmorEntityGlintRenderLayer() {
        final Identifier res = Identifier.of( "textures/misc/super_enchanted_glint_entity.png");

        return RenderLayer.of(
                "armor_entity_glint",
                RenderSetup.builder(RenderPipelines.GLINT)
                        .texture("Sampler0", res)
                        .textureTransform(TextureTransform.ARMOR_ENTITY_GLINT_TEXTURING)
                        .layeringTransform(LayeringTransform.VIEW_OFFSET_Z_LAYERING)
                        .build()
        );
    }

    private static RenderLayer buildTranslucentGlint() {
        final Identifier res = Identifier.of( "textures/misc/super_enchanted_glint_item.png");

        return RenderLayer.of(
                "glint_translucent",
                RenderSetup.builder(RenderPipelines.GLINT)
                        .texture("Sampler0", res)
                        .textureTransform(TextureTransform.GLINT_TEXTURING)
                        .outputTarget(OutputTarget.ITEM_ENTITY_TARGET)
                        .build()
        );
    }



}
