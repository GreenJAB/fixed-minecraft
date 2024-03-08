package net.greenjab.fixedminecraft

import net.fabricmc.api.ModInitializer
import net.greenjab.fixedminecraft.blocks.BlockRegistry
import net.greenjab.fixedminecraft.items.ItemGroupRegistry
import net.greenjab.fixedminecraft.items.ItemRegistry
import org.slf4j.LoggerFactory


object FixedMinecraft : ModInitializer {
    private val logger = LoggerFactory.getLogger("FixedMinecraft")

    override fun onInitialize() {
        logger.info("Initializing ${FixedMinecraftConstants.MOD_NAME}")


        //ClientSyncHandler.init()

        // ModConfig.init();
        //HUDOverlayHandler.init()

        BlockRegistry.register()
        ItemRegistry.register()

        ItemGroupRegistry.register()
    }
}
