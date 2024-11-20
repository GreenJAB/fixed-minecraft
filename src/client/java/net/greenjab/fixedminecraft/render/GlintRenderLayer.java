package net.greenjab.fixedminecraft.render;

import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderPhase;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class GlintRenderLayer extends RenderLayer{

    public static RenderLayer glintColor = buildGlintRenderLayer();
    public static RenderLayer entityGlintColor = buildEntityGlintRenderLayer();
    public static RenderLayer glintDirectColor = buildGlintDirectRenderLayer();
    public static RenderLayer entityGlintDirectColor = buildEntityGlintDirectRenderLayer();

    public static RenderLayer armorGlintColor = buildArmorGlintRenderLayer();
    public static RenderLayer armorEntityGlintColor = buildArmorEntityGlintRenderLayer();

    public static RenderLayer translucentGlintColor = buildTranslucentGlint();


    public static void addGlintTypes(Object2ObjectLinkedOpenHashMap<RenderLayer, BufferBuilder> map) {
        addGlintTypes(map, glintColor);
        addGlintTypes(map, entityGlintColor);
        addGlintTypes(map, glintDirectColor);
        addGlintTypes(map, entityGlintDirectColor);
        addGlintTypes(map, armorGlintColor);
        addGlintTypes(map, armorEntityGlintColor);
        addGlintTypes(map, translucentGlintColor);
    }

    public static void addGlintTypes(Object2ObjectLinkedOpenHashMap<RenderLayer, BufferBuilder> map, RenderLayer renderType) {
            if (!map.containsKey(renderType))
                map.put(renderType, new BufferBuilder(renderType.getExpectedBufferSize()));
    }


    public GlintRenderLayer(String name, VertexFormat vertexFormat, VertexFormat.DrawMode drawMode, int expectedBufferSize,
                            boolean hasCrumbling, boolean translucent, Runnable startAction, Runnable endAction) {
        super(name, vertexFormat, drawMode, expectedBufferSize, hasCrumbling, translucent, startAction, endAction);
    }


    private static RenderLayer buildGlintRenderLayer() {
        final Identifier res = new Identifier("textures/misc/super_enchanted_glint_item.png");

        return RenderLayer.of("glint", VertexFormats.POSITION_TEXTURE, VertexFormat.DrawMode.QUADS, 256, MultiPhaseParameters.builder()
                .program(RenderPhase.GLINT_PROGRAM)
                .texture(new Texture(res, true, false))
                .writeMaskState(COLOR_MASK)
                .cull(DISABLE_CULLING)
                .depthTest(EQUAL_DEPTH_TEST)
                .transparency(GLINT_TRANSPARENCY)
                .texturing(GLINT_TEXTURING)
                .build(false));
    }

    private static RenderLayer buildEntityGlintRenderLayer() {
        final Identifier res = new Identifier( "textures/misc/super_enchanted_glint_entity.png");

        return RenderLayer.of("entity_glint", VertexFormats.POSITION_TEXTURE, VertexFormat.DrawMode.QUADS, 256, MultiPhaseParameters.builder()
                .program(RenderPhase.ENTITY_GLINT_PROGRAM)
                .texture(new Texture(res, true, false))
                .writeMaskState(COLOR_MASK)
                .cull(DISABLE_CULLING)
                .depthTest(EQUAL_DEPTH_TEST)
                .transparency(GLINT_TRANSPARENCY)
                .target(ITEM_ENTITY_TARGET)
                .texturing(ENTITY_GLINT_TEXTURING)
                .build(false));
    }


    private static RenderLayer buildGlintDirectRenderLayer() {
        final Identifier res = new Identifier("textures/misc/super_enchanted_glint_item.png");

        return RenderLayer.of("glint_direct", VertexFormats.POSITION_TEXTURE, VertexFormat.DrawMode.QUADS, 256, MultiPhaseParameters.builder()
                .program(RenderPhase.DIRECT_GLINT_PROGRAM)
                .texture(new Texture(res, true, false))
                .writeMaskState(COLOR_MASK)
                .cull(DISABLE_CULLING)
                .depthTest(EQUAL_DEPTH_TEST)
                .transparency(GLINT_TRANSPARENCY)
                .texturing(GLINT_TEXTURING)
                .build(false));
    }


    private static RenderLayer buildEntityGlintDirectRenderLayer() {
        final Identifier res = new Identifier( "textures/misc/super_enchanted_glint_entity.png");

        return RenderLayer.of("entity_glint_direct", VertexFormats.POSITION_TEXTURE, VertexFormat.DrawMode.QUADS, 256, MultiPhaseParameters.builder()
                .program(RenderPhase.DIRECT_ENTITY_GLINT_PROGRAM)
                .texture(new Texture(res, true, false))
                .writeMaskState(COLOR_MASK)
                .cull(DISABLE_CULLING)
                .depthTest(EQUAL_DEPTH_TEST)
                .transparency(GLINT_TRANSPARENCY)
                .texturing(ENTITY_GLINT_TEXTURING)
                .build(false));
    }

    private static RenderLayer buildArmorGlintRenderLayer() {
        final Identifier res = new Identifier( "textures/misc/super_enchanted_glint_entity.png");

        return RenderLayer.of("armor_glint", VertexFormats.POSITION_TEXTURE, VertexFormat.DrawMode.QUADS, 256, MultiPhaseParameters.builder()
                .program(RenderPhase.ARMOR_GLINT_PROGRAM)
                .texture(new Texture(res, true, false))
                .writeMaskState(COLOR_MASK)
                .cull(DISABLE_CULLING)
                .depthTest(EQUAL_DEPTH_TEST)
                .transparency(GLINT_TRANSPARENCY)
                .texturing(GLINT_TEXTURING)
                .layering(VIEW_OFFSET_Z_LAYERING)
                .build(false));
    }

    private static RenderLayer buildArmorEntityGlintRenderLayer() {
        final Identifier res = new Identifier( "textures/misc/super_enchanted_glint_entity.png");

        return RenderLayer.of("armor_entity_glint", VertexFormats.POSITION_TEXTURE, VertexFormat.DrawMode.QUADS, 256, MultiPhaseParameters.builder()
                .program(RenderPhase.ARMOR_ENTITY_GLINT_PROGRAM)
                .texture(new Texture(res, true, false))
                .writeMaskState(COLOR_MASK)
                .cull(DISABLE_CULLING)
                .depthTest(EQUAL_DEPTH_TEST)
                .transparency(GLINT_TRANSPARENCY)
                .texturing(ENTITY_GLINT_TEXTURING)
                .layering(VIEW_OFFSET_Z_LAYERING)
                .build(false));
    }

    private static RenderLayer buildTranslucentGlint() {
        final Identifier res = new Identifier( "textures/misc/super_enchanted_glint_item.png");

        return RenderLayer.of("glint_translucent", VertexFormats.POSITION_TEXTURE, VertexFormat.DrawMode.QUADS, 256, RenderLayer.MultiPhaseParameters.builder()
                .program(TRANSLUCENT_GLINT_PROGRAM)
                .texture(new Texture(res, true, false))
                .writeMaskState(COLOR_MASK)
                .cull(DISABLE_CULLING)
                .depthTest(EQUAL_DEPTH_TEST)
                .transparency(GLINT_TRANSPARENCY)
                .texturing(GLINT_TEXTURING)
                .target(ITEM_ENTITY_TARGET)
                .build(false));
    }
}
