package net.greenjab.fixedminecraft

import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback
import net.fabricmc.fabric.api.resource.ResourceManagerHelper
import net.fabricmc.fabric.api.resource.ResourcePackActivationType
import net.fabricmc.loader.api.FabricLoader
import net.fabricmc.loader.api.ModContainer
import net.greenjab.fixedminecraft.models.ModelLayers
import net.greenjab.fixedminecraft.network.ClientSyncHandler

import net.greenjab.fixedminecraft.registry.BlockRegistry
import net.greenjab.fixedminecraft.registry.ItemRegistry
import net.greenjab.fixedminecraft.render.InGameHudBookPreview
import net.minecraft.client.item.ClampedModelPredicateProvider
import net.minecraft.client.item.ModelPredicateProviderRegistry
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.world.ClientWorld
import net.minecraft.entity.LivingEntity
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.text.Text
import net.minecraft.util.Identifier

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
            BlockRegistry.WAXED_OXIDIZED_COPPER_RAIL,
        )

        HudRenderCallback.EVENT.register(InGameHudBookPreview::renderCrosshair)

        ModelLayers.onRegisterLayers()

        ModelPredicateProviderRegistry.register(
            Items.TOTEM_OF_UNDYING,
            Identifier("saving"),
            ClampedModelPredicateProvider { stack: ItemStack, world: ClientWorld?, entity: LivingEntity?, seed: Int -> if (entity != null && entity.isUsingItem && entity.activeItem == stack) 1.0f else 0.0f })

        ModelPredicateProviderRegistry.register(
            ItemRegistry.ECHO_TOTEM,
            Identifier("saving"),
            ClampedModelPredicateProvider { stack: ItemStack, world: ClientWorld?, entity: LivingEntity?, seed: Int -> if (entity != null && entity.isUsingItem && entity.activeItem == stack) 1.0f else 0.0f })

        FabricLoader.getInstance().getModContainer("fixedminecraft").ifPresent { modContainer: ModContainer? ->
            ResourceManagerHelper.registerBuiltinResourcePack(
                Identifier("fixedminecraft", "greentweaks"),
                modContainer,
                Text.of("Green Tweaks"),
                ResourcePackActivationType.DEFAULT_ENABLED
            )
        }
    }
}
