package net.greenjab.fixedminecraft

import net.fabricmc.api.ModInitializer
import net.greenjab.fixedminecraft.blocks.BlockRegistry
import net.greenjab.fixedminecraft.items.ItemGroupRegistry
import net.greenjab.fixedminecraft.items.ItemRegistry
import org.slf4j.kotlin.getLogger
import org.slf4j.kotlin.info


object FixedMinecraft : ModInitializer {
    private val logger by getLogger()

    override fun onInitialize() {
        logger.info { "Initializing ${FixedMinecraftConstants.MOD_NAME}" }

        BlockRegistry.register()
        ItemRegistry.register()

        ItemGroupRegistry.register()
    }
}
