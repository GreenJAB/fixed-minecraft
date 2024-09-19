package net.greenjab.fixedminecraft.glint

import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.render.BufferBuilder
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.VertexFormat
import net.minecraft.client.render.VertexFormat.DrawMode
import net.minecraft.client.render.VertexFormats
import net.minecraft.util.DyeColor
import net.minecraft.util.Identifier
import java.util.function.Function

@Environment(EnvType.CLIENT)
class GlintRenderLayer(
    name: String?,
    vertexFormat: VertexFormat?,
    drawMode: DrawMode?,
    expectedBufferSize: Int,
    hasCrumbling: Boolean,
    translucent: Boolean,
    startAction: Runnable?,
    endAction: Runnable?
) :
    RenderLayer(name, vertexFormat, drawMode, expectedBufferSize, hasCrumbling, translucent, startAction, endAction) {
    companion object {
        var glintColor: RenderLayer = buildGlintRenderLayer()
        var entityGlintColor: RenderLayer = buildEntityGlintRenderLayer()
        var glintDirectColor: RenderLayer = buildGlintDirectRenderLayer()
        var entityGlintDirectColor: RenderLayer = buildEntityGlintDirectRenderLayer()
        var armorGlintColor: RenderLayer = buildArmorGlintRenderLayer()
        var armorEntityGlintColor: RenderLayer = buildArmorEntityGlintRenderLayer()
        var translucentGlintColor: RenderLayer = buildTranslucentGlint()

        /*fun addGlintTypes(map: Object2ObjectLinkedOpenHashMap<RenderLayer?, BufferBuilder?>) {
            addGlintTypes(map, glintColor)
            addGlintTypes(map, entityGlintColor)
            addGlintTypes(map, glintDirectColor)
            addGlintTypes(map, entityGlintDirectColor)
            addGlintTypes(map, armorGlintColor)
            addGlintTypes(map, armorEntityGlintColor)
            addGlintTypes(map, translucentGlintColor)
        }*/

        /*private fun newRenderList(func: Function<String, RenderLayer>): List<RenderLayer> {
            val list = ArrayList<RenderLayer>(DyeColor.entries.size)

            for (color in DyeColor.entries) list.add(func.apply(color.getName()))

            return list
        }*/

        /*fun addGlintTypes(map: Object2ObjectLinkedOpenHashMap<RenderLayer?, BufferBuilder?>, typeList: List<RenderLayer>) {
            for (renderType in typeList) if (!map.containsKey(renderType)) map[renderType] = BufferBuilder(renderType.expectedBufferSize)
        }*/

        private fun buildGlintRenderLayer(): RenderLayer {
            val res = Identifier("textures/misc/super_enchanted_glint_item.png")

            return of(
                "glint_", VertexFormats.POSITION_TEXTURE, DrawMode.QUADS, 256, MultiPhaseParameters.builder()
                    .program(GLINT_PROGRAM)
                    .texture(Texture(res, true, false))
                    .writeMaskState(COLOR_MASK)
                    .cull(DISABLE_CULLING)
                    .depthTest(EQUAL_DEPTH_TEST)
                    .transparency(GLINT_TRANSPARENCY)
                    .texturing(GLINT_TEXTURING)
                    .build(false)
            )
        }

        private fun buildEntityGlintRenderLayer(): RenderLayer {
            val res = Identifier("textures/misc/super_enchanted_glint_entity.png")

            return of(
                "entity_glint_", VertexFormats.POSITION_TEXTURE, DrawMode.QUADS, 256, MultiPhaseParameters.builder()
                    .program(ENTITY_GLINT_PROGRAM)
                    .texture(Texture(res, true, false))
                    .writeMaskState(COLOR_MASK)
                    .cull(DISABLE_CULLING)
                    .depthTest(EQUAL_DEPTH_TEST)
                    .transparency(GLINT_TRANSPARENCY)
                    .target(ITEM_ENTITY_TARGET)
                    .texturing(ENTITY_GLINT_TEXTURING)
                    .build(false)
            )
        }


        private fun buildGlintDirectRenderLayer(): RenderLayer {
            val res = Identifier("textures/misc/super_enchanted_glint_item.png")

            return of(
                "glint_direct_", VertexFormats.POSITION_TEXTURE, DrawMode.QUADS, 256, MultiPhaseParameters.builder()
                    .program(DIRECT_GLINT_PROGRAM)
                    .texture(Texture(res, true, false))
                    .writeMaskState(COLOR_MASK)
                    .cull(DISABLE_CULLING)
                    .depthTest(EQUAL_DEPTH_TEST)
                    .transparency(GLINT_TRANSPARENCY)
                    .texturing(GLINT_TEXTURING)
                    .build(false)
            )
        }


        private fun buildEntityGlintDirectRenderLayer(): RenderLayer {
            val res = Identifier("textures/misc/super_enchanted_glint_entity.png")

            return of(
                "entity_glint_direct_", VertexFormats.POSITION_TEXTURE, DrawMode.QUADS, 256, MultiPhaseParameters.builder()
                    .program(DIRECT_ENTITY_GLINT_PROGRAM)
                    .texture(Texture(res, true, false))
                    .writeMaskState(COLOR_MASK)
                    .cull(DISABLE_CULLING)
                    .depthTest(EQUAL_DEPTH_TEST)
                    .transparency(GLINT_TRANSPARENCY)
                    .texturing(ENTITY_GLINT_TEXTURING)
                    .build(false)
            )
        }

        private fun buildArmorGlintRenderLayer(): RenderLayer {
            val res = Identifier("textures/misc/super_enchanted_glint_entity.png")

            return of(
                "armor_glint_", VertexFormats.POSITION_TEXTURE, DrawMode.QUADS, 256, MultiPhaseParameters.builder()
                    .program(ARMOR_GLINT_PROGRAM)
                    .texture(Texture(res, true, false))
                    .writeMaskState(COLOR_MASK)
                    .cull(DISABLE_CULLING)
                    .depthTest(EQUAL_DEPTH_TEST)
                    .transparency(GLINT_TRANSPARENCY)
                    .texturing(GLINT_TEXTURING)
                    .layering(VIEW_OFFSET_Z_LAYERING)
                    .build(false)
            )
        }

        private fun buildArmorEntityGlintRenderLayer(): RenderLayer {
            val res = Identifier("textures/misc/super_enchanted_glint_entity.png")

            return of(
                "armor_entity_glint_e", VertexFormats.POSITION_TEXTURE, DrawMode.QUADS, 256, MultiPhaseParameters.builder()
                    .program(ARMOR_ENTITY_GLINT_PROGRAM)
                    .texture(Texture(res, true, false))
                    .writeMaskState(COLOR_MASK)
                    .cull(DISABLE_CULLING)
                    .depthTest(EQUAL_DEPTH_TEST)
                    .transparency(GLINT_TRANSPARENCY)
                    .texturing(ENTITY_GLINT_TEXTURING)
                    .layering(VIEW_OFFSET_Z_LAYERING)
                    .build(false)
            )
        }

        private fun buildTranslucentGlint(): RenderLayer {
            val res = Identifier("textures/misc/super_enchanted_glint_item.png")

            return of(
                "glint_translucent_", VertexFormats.POSITION_TEXTURE, DrawMode.QUADS, 256, MultiPhaseParameters.builder()
                    .program(TRANSLUCENT_GLINT_PROGRAM)
                    .texture(Texture(res, true, false))
                    .writeMaskState(COLOR_MASK)
                    .cull(DISABLE_CULLING)
                    .depthTest(EQUAL_DEPTH_TEST)
                    .transparency(GLINT_TRANSPARENCY)
                    .texturing(GLINT_TEXTURING)
                    .target(ITEM_ENTITY_TARGET)
                    .build(false)
            )
        }
    }
}
