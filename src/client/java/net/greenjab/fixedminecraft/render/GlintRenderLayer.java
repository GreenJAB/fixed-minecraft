package net.greenjab.fixedminecraft.render;

import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderPhase;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.BufferAllocator;
import net.minecraft.util.Identifier;

/** Credit: Pepperoni-Jabroni */
@Environment(EnvType.CLIENT)
public class GlintRenderLayer extends RenderLayer{

    public static RenderLayer glintColor = buildGlintRenderLayer();
    public static RenderLayer directGlintColor = buildDirectGlintRenderLayer();
    public static RenderLayer entityGlintColor = buildEntityGlintRenderLayer();
    public static RenderLayer armorEntityGlintColor = buildArmorEntityGlintRenderLayer();
    public static RenderLayer translucentGlintColor = buildTranslucentGlint();

    public static void addGlintTypes(Object2ObjectLinkedOpenHashMap<RenderLayer, BufferAllocator> map) {
        addGlintTypes(map, glintColor);
        addGlintTypes(map, directGlintColor);
        addGlintTypes(map, entityGlintColor);
        addGlintTypes(map, armorEntityGlintColor);
        addGlintTypes(map, translucentGlintColor);
    }

    public static void addGlintTypes(Object2ObjectLinkedOpenHashMap<RenderLayer, BufferAllocator> map, RenderLayer renderType) {
            if (!map.containsKey(renderType))
                map.put(renderType, new BufferAllocator(renderType.getExpectedBufferSize()));
    }


    public GlintRenderLayer(String name, VertexFormat vertexFormat, VertexFormat.DrawMode drawMode, int expectedBufferSize,
                            boolean hasCrumbling, boolean translucent, Runnable startAction, Runnable endAction) {
        super(name, vertexFormat, drawMode, expectedBufferSize, hasCrumbling, translucent, startAction, endAction);
    }


    private static RenderLayer buildGlintRenderLayer() {
        final Identifier res = Identifier.of("textures/misc/super_enchanted_glint_item.png");

        return RenderLayer.of("glint", VertexFormats.POSITION_TEXTURE, VertexFormat.DrawMode.QUADS, 256, MultiPhaseParameters.builder()
                .program(RenderPhase.GLINT_PROGRAM)
                .texture(new Texture(res, false, false))
                .writeMaskState(COLOR_MASK)
                .cull(DISABLE_CULLING)
                .depthTest(EQUAL_DEPTH_TEST)
                .transparency(GLINT_TRANSPARENCY)
                .texturing(GLINT_TEXTURING)
                .build(false));
    }

    private static RenderLayer buildDirectGlintRenderLayer() {
        final Identifier res = Identifier.of("textures/misc/super_enchanted_glint_item.png");

        return RenderLayer.of(
                "entity_glint_direct",
                VertexFormats.POSITION_TEXTURE,
                VertexFormat.DrawMode.QUADS,
                1536,
                RenderLayer.MultiPhaseParameters.builder()
                        .program(DIRECT_ENTITY_GLINT_PROGRAM)
                        .texture(new RenderPhase.Texture(ItemRenderer.ENTITY_ENCHANTMENT_GLINT, true, false))
                        .writeMaskState(COLOR_MASK)
                        .cull(DISABLE_CULLING)
                        .depthTest(EQUAL_DEPTH_TEST)
                        .transparency(GLINT_TRANSPARENCY)
                        .texturing(ENTITY_GLINT_TEXTURING)
                        .build(false)
        );
    }

    private static RenderLayer buildEntityGlintRenderLayer() {
        final Identifier res = Identifier.of( "textures/misc/super_enchanted_glint_entity.png");

        return RenderLayer.of("entity_glint", VertexFormats.POSITION_TEXTURE, VertexFormat.DrawMode.QUADS, 256, MultiPhaseParameters.builder()
                .program(RenderPhase.ENTITY_GLINT_PROGRAM)
                .texture(new Texture(res, false, false))
                .writeMaskState(COLOR_MASK)
                .cull(DISABLE_CULLING)
                .depthTest(EQUAL_DEPTH_TEST)
                .transparency(GLINT_TRANSPARENCY)
                .target(ITEM_ENTITY_TARGET)
                .texturing(ENTITY_GLINT_TEXTURING)
                .build(false));
    }

    private static RenderLayer buildArmorEntityGlintRenderLayer() {
        final Identifier res = Identifier.of( "textures/misc/super_enchanted_glint_entity.png");

        return RenderLayer.of("armor_entity_glint", VertexFormats.POSITION_TEXTURE, VertexFormat.DrawMode.QUADS, 256, MultiPhaseParameters.builder()
                .program(RenderPhase.ARMOR_ENTITY_GLINT_PROGRAM)
                .texture(new Texture(res, false, false))
                .writeMaskState(COLOR_MASK)
                .cull(DISABLE_CULLING)
                .depthTest(EQUAL_DEPTH_TEST)
                .transparency(GLINT_TRANSPARENCY)
                .texturing(ENTITY_GLINT_TEXTURING)
                .layering(VIEW_OFFSET_Z_LAYERING)
                .build(false));
    }

    private static RenderLayer buildTranslucentGlint() {
        final Identifier res = Identifier.of( "textures/misc/super_enchanted_glint_item.png");

        return RenderLayer.of("glint_translucent", VertexFormats.POSITION_TEXTURE, VertexFormat.DrawMode.QUADS, 256, RenderLayer.MultiPhaseParameters.builder()
                .program(TRANSLUCENT_GLINT_PROGRAM)
                .texture(new Texture(res, false, false))
                .writeMaskState(COLOR_MASK)
                .cull(DISABLE_CULLING)
                .depthTest(EQUAL_DEPTH_TEST)
                .transparency(GLINT_TRANSPARENCY)
                .texturing(GLINT_TEXTURING)
                .target(ITEM_ENTITY_TARGET)
                .build(false));
    }
}
