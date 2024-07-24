package net.greenjab.fixedminecraft

import net.fabricmc.api.ModInitializer
import net.greenjab.fixedminecraft.StatusEffects.StatusRegistry

import net.greenjab.fixedminecraft.registry.BlockRegistry
import net.greenjab.fixedminecraft.registry.ItemGroupRegistry
import net.greenjab.fixedminecraft.registry.ItemRegistry
import net.minecraft.util.Identifier
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
    }

    fun id(path: String): Identifier? {
        return Identifier.of(FixedMinecraftConstants.NAMESPACE, path)
    }
}