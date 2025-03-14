package net.greenjab.fixedminecraft

import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback
import net.fabricmc.fabric.api.resource.ResourceManagerHelper
import net.fabricmc.fabric.api.resource.ResourcePackActivationType
import net.fabricmc.loader.api.FabricLoader
import net.fabricmc.loader.api.ModContainer
import net.greenjab.fixedminecraft.map_book.MapBookFilledProperty
import net.greenjab.fixedminecraft.models.ModelLayers
import net.greenjab.fixedminecraft.network.ClientSyncHandler
import net.greenjab.fixedminecraft.registry.registries.BlockRegistry
import net.greenjab.fixedminecraft.render.PlayerLookHelper
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.RenderTickCounter
import net.minecraft.client.render.item.property.bool.BooleanProperties
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.text.Text
import net.minecraft.util.Identifier

object FixedMinecraftClient : ClientModInitializer {
    var paleGardenFog = 0f
    override fun onInitializeClient() {

        ClientSyncHandler.init()

        BlockRenderLayerMap.INSTANCE.putBlocks(
            RenderLayer.getCutout(),
            BlockRegistry.COPPER_RAIL,
            BlockRegistry.EXPOSED_COPPER_RAIL,
            BlockRegistry.WEATHERED_COPPER_RAIL,
            BlockRegistry.OXIDIZED_COPPER_RAIL,
            BlockRegistry.WAXED_COPPER_RAIL,
            BlockRegistry.WAXED_EXPOSED_COPPER_RAIL,
            BlockRegistry.WAXED_WEATHERED_COPPER_RAIL,
            BlockRegistry.WAXED_OXIDIZED_COPPER_RAIL,
        )


        HudRenderCallback.EVENT.register(HudRenderCallback { drawContext: DrawContext, tickDeltaManager: RenderTickCounter ->
            val matrices = drawContext.matrices
            matrices.push()


            val client = MinecraftClient.getInstance()

            val book = PlayerLookHelper.getLookingAtBook(null)
            if (book != ItemStack.EMPTY) {

            val display = PlayerLookHelper.getBookText(book)
            for (i in display.indices) {
                val text = display[i]
                drawContext.drawText(
                    client.textRenderer,
                    text,
                    (client.window.scaledWidth / 2.0 - client.textRenderer.getWidth(text) / 2).toInt(),
                    (client.window.scaledHeight / 2.0 + 15 + (i * 10)).toInt(),
                    if ((book.item === Items.ENCHANTED_BOOK && i == 0)) 16777045 else 16777215,
                    true
                )
            }
            }

            matrices.pop()
        })

        BooleanProperties.ID_MAPPER.put(FixedMinecraft.id("map_book/filled"), MapBookFilledProperty.CODEC)
        ModelLayers.onRegisterLayers()

        FabricLoader.getInstance().getModContainer("fixedminecraft").ifPresent { modContainer: ModContainer? ->
            ResourceManagerHelper.registerBuiltinResourcePack(
                Identifier.of("fixedminecraft", "greentweaks"),
                modContainer,
                Text.of("Green Tweaks"),
                ResourcePackActivationType.DEFAULT_ENABLED
            )
        }
    }
}
