package net.greenjab.fixedminecraft.render;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexFormat;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.render.BuiltBuffer;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderPhase;
import net.minecraft.client.util.BufferAllocator;
import net.minecraft.util.Identifier;
import net.minecraft.util.TriState;
import net.minecraft.util.Util;
import org.joml.Matrix4f;

/** Credit: Pepperoni-Jabroni */
@Environment(EnvType.CLIENT)
public class GlintRenderLayer extends RenderLayer{

    public static RenderLayer glintColor = buildGlintRenderLayer();
    public static RenderLayer entityGlintColor = buildEntityGlintRenderLayer();
    public static RenderLayer armorEntityGlintColor = buildArmorEntityGlintRenderLayer();
    public static RenderLayer translucentGlintColor = buildTranslucentGlint();

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

    public GlintRenderLayer(String name, int size, boolean hasCrumbling, boolean translucent, Runnable begin, Runnable end) {
        super(name, size, hasCrumbling, translucent, begin, end);
    }

    private static RenderLayer buildGlintRenderLayer() {
        final Identifier res = Identifier.of("textures/misc/super_enchanted_glint_item.png");

        return RenderLayer.of(
                "glint",
                1536,
                RenderPipelines.GLINT,
                RenderLayer.MultiPhaseParameters.builder()
                        .texture(new RenderPhase.Texture(res, TriState.DEFAULT, false))
                        .texturing(GLINT_TEXTURING)
                        .build(false)
        );
    }

    private static RenderLayer buildEntityGlintRenderLayer() {
        final Identifier res = Identifier.of( "textures/misc/super_enchanted_glint_entity.png");

        return RenderLayer.of(
                "entity_glint",
                1536,
                RenderPipelines.GLINT,
                RenderLayer.MultiPhaseParameters.builder()
                        .texture(new RenderPhase.Texture(res, TriState.DEFAULT, false))
                        .texturing(ENTITY_GLINT_TEXTURING)
                        .build(false)
        );
    }

    private static RenderLayer buildArmorEntityGlintRenderLayer() {
        final Identifier res = Identifier.of( "textures/misc/super_enchanted_glint_entity.png");

        return RenderLayer.of(
                "armor_entity_glint",
                1536,
                RenderPipelines.GLINT,
                RenderLayer.MultiPhaseParameters.builder()
                        .texture(new RenderPhase.Texture(res, TriState.DEFAULT, false))
                        .texturing(ARMOR_ENTITY_GLINT_TEXTURING)
                        .layering(VIEW_OFFSET_Z_LAYERING)
                        .build(false));
    }

    private static RenderLayer buildTranslucentGlint() {
        final Identifier res = Identifier.of( "textures/misc/super_enchanted_glint_item.png");

        return RenderLayer.of("glint_translucent", 1536, RenderPipelines.GLINT,RenderLayer.MultiPhaseParameters.builder()
                .texture(new RenderPhase.Texture(res, TriState.DEFAULT, false))
                .texturing(GLINT_TEXTURING)
                .target(ITEM_ENTITY_TARGET)
                .build(false));
    }

    @Override
    public void draw(BuiltBuffer buffer) {

    }

    @Override
    public Framebuffer getTarget() {
        return null;
    }

    @Override
    public RenderPipeline getPipeline() {
        return null;
    }

    @Override
    public VertexFormat getVertexFormat() {
        return null;
    }

    @Override
    public VertexFormat.DrawMode getDrawMode() {
        return null;
    }
}
