package net.greenjab.fixedminecraft

//import net.greenjab.fixedminecraft.network.ClientSyncManager
import net.fabricmc.api.ModInitializer
import net.greenjab.fixedminecraft.registry.BlockRegistry
import net.greenjab.fixedminecraft.registry.ItemGroupRegistry
import net.greenjab.fixedminecraft.registry.ItemRegistry
import org.slf4j.LoggerFactory


object FixedMinecraft : ModInitializer {
    private val logger = LoggerFactory.getLogger("FixedMinecraft")

    override fun onInitialize() {
        logger.info("Initializing ${FixedMinecraftConstants.MOD_NAME}")

         //ClientSyncHandler.init()

        // ModConfig.init();
        // HUDOverlayHandler.init()

        BlockRegistry.register()
        ItemRegistry.register()
        ItemGroupRegistry.register()
    }
}
