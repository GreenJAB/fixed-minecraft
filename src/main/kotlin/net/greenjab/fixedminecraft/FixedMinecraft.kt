package net.greenjab.fixedminecraft

import net.fabricmc.api.ModInitializer
import net.greenjab.fixedminecraft.StatusEffects.StatusRegistry
import net.greenjab.fixedminecraft.blocks.BlockRegistry
import net.greenjab.fixedminecraft.items.ItemGroupRegistry
import net.greenjab.fixedminecraft.items.ItemRegistry
import net.greenjab.fixedminecraft.items.RecipeRegistry
//import net.greenjab.fixedminecraft.network.ClientSyncManager
import org.slf4j.LoggerFactory


object FixedMinecraft : ModInitializer {
    val logger = LoggerFactory.getLogger("FixedMinecraft")

    override fun onInitialize() {
        logger.info("Initializing ${FixedMinecraftConstants.MOD_NAME}")

         //ClientSyncHandler.init()

        // ModConfig.init();
        // HUDOverlayHandler.init()

        BlockRegistry.register()
        ItemRegistry.register()
        StatusRegistry.register()

        ItemGroupRegistry.register()

        RecipeRegistry.register()
    }
}
