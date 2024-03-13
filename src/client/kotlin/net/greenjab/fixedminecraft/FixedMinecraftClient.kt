package net.greenjab.fixedminecraft

import net.fabricmc.api.ClientModInitializer
import net.greenjab.fixedminecraft.network.ClientSyncHandler
import net.minecraft.client.item.ClampedModelPredicateProvider
import net.minecraft.client.item.ModelPredicateProviderRegistry
import net.minecraft.client.world.ClientWorld
import net.minecraft.entity.LivingEntity
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.util.Identifier


object FixedMinecraftClient : ClientModInitializer {
    override fun onInitializeClient() {
        ClientSyncHandler.init()

        /*ModelPredicateProviderRegistry.register(
            Items.TOTEM_OF_UNDYING,
            Identifier("saving"),
            ClampedModelPredicateProvider { stack: ItemStack, world: ClientWorld?, entity: LivingEntity?, seed: Int ->
                entity != null && entity.isUsingItem && entity.activeItem == stack 1.0f else 0.0f
            })
*/
    }
}
