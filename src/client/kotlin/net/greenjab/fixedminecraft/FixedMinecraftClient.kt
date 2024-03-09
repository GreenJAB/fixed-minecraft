package net.greenjab.fixedminecraft

import net.fabricmc.api.ClientModInitializer
import net.greenjab.fixedminecraft.network.ClientSyncHandler

object FixedMinecraftClient : ClientModInitializer {
    override fun onInitializeClient() {
        ClientSyncHandler.init()
    }
}
