package net.greenjab.fixedminecraft

import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap
import net.greenjab.fixedminecraft.network.ClientSyncHandler
import net.greenjab.fixedminecraft.registry.BlockRegistry
import net.minecraft.client.render.RenderLayer

object FixedMinecraftClient : ClientModInitializer {
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
            BlockRegistry.WAXED_OXIDIZED_COPPER_RAIL
        )
    }
}
