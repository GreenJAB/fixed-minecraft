package net.greenjab.fixedminecraft;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.greenjab.fixedminecraft.hud.HotbarCycler;
import net.greenjab.fixedminecraft.map_book.MapBookFilledProperty;
import net.greenjab.fixedminecraft.models.CustomModelLayers;
import net.greenjab.fixedminecraft.registries.CustomEntityModelLayerRegistry;
import net.greenjab.fixedminecraft.registries.EntityRendererRegistry;
import net.greenjab.fixedminecraft.screens.FletchingScreen;
import net.greenjab.fixedminecraft.screens.NewAnvilScreen;
import net.greenjab.fixedminecraft.screens.NewEnchantmentScreen;
import net.greenjab.fixedminecraft.registry.registries.MenuRegistry;
import net.greenjab.fixedminecraft.render.ChiseledBookRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.item.properties.conditional.ConditionalItemModelProperties;
import net.minecraft.network.chat.Component;

public class FixedMinecraftClient implements ClientModInitializer {
    public static float paleGardenFog = 0f;
    public static float voidFog = 0f;
    public static boolean fontLegend = false;

    public static OptionInstance<Boolean> itemArmorHud = OptionInstance.createBoolean("options.itemArmorHud", true);
    public static OptionInstance<Boolean> jabsFixedFog = OptionInstance.createBoolean("options.jabsFixedFog", true);
    public static OptionInstance<Boolean> villagersSpeak = OptionInstance.createBoolean("options.chat.villagersSpeak", true);

    @Override
    public void onInitializeClient() {

        ClientSyncHandler.init();

        MenuScreens.register(MenuRegistry.FLETCHING_SCREEN_HANDLER, FletchingScreen::new);
        MenuScreens.register(MenuRegistry.NEW_ENCHANTMENT_SCREEN_HANDLER, NewEnchantmentScreen::new);
        MenuScreens.register(MenuRegistry.NEW_ANVIL_SCREEN_HANDLER, NewAnvilScreen::new);

        HotbarCycler.register();

        ChiseledBookRenderer CBR = new ChiseledBookRenderer();
        HudElementRegistry.addLast(FixedMinecraft.id("chiseled_book"), CBR);

        ConditionalItemModelProperties.ID_MAPPER.put(FixedMinecraft.id("map_book/filled"), MapBookFilledProperty.CODEC);
        CustomModelLayers.onRegisterLayers();
        EntityRendererRegistry.registerEntityRenderer();
        CustomEntityModelLayerRegistry.registerEntityModelLayer();

        FabricLoader.getInstance().getModContainer("fixedminecraft").ifPresent(modContainer -> {
            ResourceManagerHelper.registerBuiltinResourcePack(
                    FixedMinecraft.id( "green_tweaks"),
                    modContainer,
                    Component.translatable("fixedminecraft.green_tweaks"),
                    ResourcePackActivationType.NORMAL
            );
            ResourceManagerHelper.registerBuiltinResourcePack(
                    FixedMinecraft.id( "recolourful_containers"),
                    modContainer,
                    Component.translatable("fixedminecraft.recolourful_containers"),
                    ResourcePackActivationType.NORMAL
            );
            ResourceManagerHelper.registerBuiltinResourcePack(
                    FixedMinecraft.id( "re_covered"),
                    modContainer,
                    Component.translatable("fixedminecraft.re_covered"),
                    ResourcePackActivationType.NORMAL
            );
            ResourceManagerHelper.registerBuiltinResourcePack(
                    FixedMinecraft.id( "almost_vanilla_potions"),
                    modContainer,
                    Component.translatable("fixedminecraft.almost_vanilla_potions"),
                    ResourcePackActivationType.NORMAL
            );
            ResourceManagerHelper.registerBuiltinResourcePack(
                    FixedMinecraft.id( "fixed_pacp"),
                    modContainer,
                    Component.translatable("fixedminecraft.fixed_pacp"),
                    ResourcePackActivationType.NORMAL
            );
        });
    }

    public static boolean usingCustomContainers() {
        return (Minecraft.getInstance().getResourcePackRepository().getSelectedPacks().stream().anyMatch(pack -> pack.location().id().toLowerCase().contains("recolourful_containers")));
    }
}
