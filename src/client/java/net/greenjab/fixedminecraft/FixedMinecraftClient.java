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
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public class FixedMinecraftClient implements ClientModInitializer {
    public static float paleGardenFog = 0f;
    public static float voidFog = 0f;
    public static EquipmentClientInfo chainmailModel = createHumanoidAndHorseModel("chainmail");
    public static EquipmentClientInfo copperExposedModel = createHumanoidOnlyModel("copper_exposed");
    public static EquipmentClientInfo copperWeatheredModel = createHumanoidOnlyModel("copper_weathered");
    public static EquipmentClientInfo copperOxidizedModel = createHumanoidOnlyModel("copper_oxidized");
    public static EquipmentClientInfo scuteNautilusArmor = EquipmentClientInfo.builder()
            .addLayers(EquipmentClientInfo.LayerType.NAUTILUS_BODY, EquipmentClientInfo.Layer.onlyIfDyed(Identifier.withDefaultNamespace("armadillo_scute"), false))
            .addLayers(EquipmentClientInfo.LayerType.NAUTILUS_BODY, EquipmentClientInfo.Layer.onlyIfDyed(Identifier.withDefaultNamespace("armadillo_scute_overlay"), true))
            .build();
    public static OptionInstance<Boolean> newArmorHud = OptionInstance.createBoolean("options.newArmorHud", true);
    public static OptionInstance<Boolean> fog_21_6 = OptionInstance.createBoolean("options.fog_21_6", true);

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

        FabricLoader.getInstance().getModContainer("fixedminecraft").ifPresent(modContainer ->
                ResourceManagerHelper.registerBuiltinResourcePack(
                FixedMinecraft.id( "greentweaks"),
                modContainer,
                Component.translatable("fixed.green_tweaks"),
                ResourcePackActivationType.NORMAL
        ));
    }
    private static EquipmentClientInfo createHumanoidAndHorseModel(String id) {
        return EquipmentClientInfo.builder()
                .addHumanoidLayers(Identifier.withDefaultNamespace(id))
                .addLayers(EquipmentClientInfo.LayerType.HORSE_BODY, EquipmentClientInfo.Layer.leatherDyeable(Identifier.withDefaultNamespace(id), false))
                .build();
    }
    private static EquipmentClientInfo createHumanoidOnlyModel(String id) {
        return EquipmentClientInfo.builder()
                .addHumanoidLayers(Identifier.withDefaultNamespace(id))
                .build();
    }

    public static boolean usingCustomContainers() {
        return (Minecraft.getInstance().getResourcePackRepository().getSelectedPacks().stream().anyMatch(pack -> pack.getTitle().getString().toLowerCase().contains("recolourful containers")));
    }
}
