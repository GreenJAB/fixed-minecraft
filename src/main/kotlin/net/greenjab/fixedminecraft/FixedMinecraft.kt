package net.greenjab.fixedminecraft

import net.fabricmc.api.ModInitializer
import net.greenjab.fixedminecraft.network.SyncHandler
import net.greenjab.fixedminecraft.registry.registries.ItemGroupRegistry
import net.greenjab.fixedminecraft.registry.registries.GameruleRegistry
import net.greenjab.fixedminecraft.registry.registries.RecipeRegistry
import net.minecraft.block.DispenserBlock
import net.minecraft.item.Item
import net.minecraft.item.Items
import net.minecraft.server.MinecraftServer
import net.minecraft.util.Identifier
import org.slf4j.LoggerFactory
import java.util.HashMap


object FixedMinecraft : ModInitializer {
    val logger = LoggerFactory.getLogger("FixedMinecraft")
    var SERVER: MinecraftServer? = null

    var netheriteAnvil = false
    var ItemCapacities : HashMap<Item, Int> = hashMapOf()

    //TODO Rename functions / commenting
    //TODO biome colours?
    //TODO Pale garden ominous biome?
    //TODO Remove Kotlin
    //TODO LOTS of bug testing

    override fun onInitialize() {
        logger.info("Initializing ${FixedMinecraftConstants.MOD_NAME}")

        SyncHandler.init()

        ItemGroupRegistry.register()

        RecipeRegistry.register()
        GameruleRegistry.register()

        DispenserBlock.registerProjectileBehavior(Items.BRICK)
        DispenserBlock.registerProjectileBehavior(Items.NETHER_BRICK)
        DispenserBlock.registerProjectileBehavior(Items.RESIN_BRICK)
        DispenserBlock.registerProjectileBehavior(Items.TRIDENT)
    }

    fun id(path: String): Identifier? {
        return Identifier.of(FixedMinecraftConstants.NAMESPACE, path)
    }
}
