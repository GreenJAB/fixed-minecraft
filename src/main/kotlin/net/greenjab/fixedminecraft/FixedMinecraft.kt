package net.greenjab.fixedminecraft

import net.fabricmc.api.ModInitializer
import net.greenjab.fixedminecraft.StatusEffects.StatusRegistry
import net.greenjab.fixedminecraft.network.SyncHandler
import net.greenjab.fixedminecraft.registry.GameruleRegistry
import net.greenjab.fixedminecraft.registry.ItemGroupRegistry
import net.greenjab.fixedminecraft.registry.LoottableRegistry
import net.greenjab.fixedminecraft.registry.RecipeRegistry
import net.minecraft.block.DispenserBlock
import net.minecraft.item.Items
import net.minecraft.server.MinecraftServer
import net.minecraft.util.Identifier
import org.slf4j.LoggerFactory


object FixedMinecraft : ModInitializer {
    val logger = LoggerFactory.getLogger("FixedMinecraft")
    var SERVER: MinecraftServer? = null

    override fun onInitialize() {
        logger.info("Initializing ${FixedMinecraftConstants.MOD_NAME}")

         // ClientSyncHandler.init()

        // ModConfig.init();
        // HUDOverlayHandler.init()

        // BlockRegistry2.register()
        // ItemRegistry3.register()

        SyncHandler.init();

        ItemGroupRegistry.register()

        RecipeRegistry.register()
        GameruleRegistry.register()
        LoottableRegistry.register()

        StatusRegistry.register()

        DispenserBlock.registerProjectileBehavior(Items.BRICK)
        DispenserBlock.registerProjectileBehavior(Items.NETHER_BRICK)
        DispenserBlock.registerProjectileBehavior(Items.TRIDENT)
    }

    fun id(path: String): Identifier? {
        return Identifier.of(FixedMinecraftConstants.NAMESPACE, path)
    }
}
