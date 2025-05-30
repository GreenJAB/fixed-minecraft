package net.greenjab.fixedminecraft;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.greenjab.fixedminecraft.map_book.MapBookFilledProperty;
import net.greenjab.fixedminecraft.models.ModelLayers;
import net.greenjab.fixedminecraft.registry.registries.BlockRegistry;
import net.greenjab.fixedminecraft.render.PlayerLookHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.render.entity.equipment.EquipmentModel;
import net.minecraft.client.render.item.property.bool.BooleanProperties;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.List;

public class FixedMinecraftClient implements ClientModInitializer {
    public static float paleGardenFog = 0f;
    public static EquipmentModel netheriteModel = createHumanoidAndHorseModel("netherite");
    public static EquipmentModel chainmailModel = createHumanoidAndHorseModel("chainmail");
    public static SimpleOption<Boolean> newArmorHud = SimpleOption.ofBoolean("options.newArmorHud", true);

    @Override
    public void onInitializeClient() {

        ClientSyncHandler.init();

        BlockRenderLayerMap.INSTANCE.putBlocks(
                RenderLayer.getCutout(),
                BlockRegistry.COPPER_RAIL,
                BlockRegistry.EXPOSED_COPPER_RAIL,
                BlockRegistry.WEATHERED_COPPER_RAIL,
                BlockRegistry.OXIDIZED_COPPER_RAIL,
                BlockRegistry.WAXED_COPPER_RAIL,
                BlockRegistry.WAXED_EXPOSED_COPPER_RAIL,
                BlockRegistry.WAXED_WEATHERED_COPPER_RAIL,
                BlockRegistry.WAXED_OXIDIZED_COPPER_RAIL
                );

        HudRenderCallback.EVENT.register(this::renderCrosshair);

        BooleanProperties.ID_MAPPER.put(FixedMinecraft.id("map_book/filled"), MapBookFilledProperty.CODEC);
        ModelLayers.onRegisterLayers();

        FabricLoader.getInstance().getModContainer("fixedminecraft").ifPresent(modContainer -> {
            ResourceManagerHelper.registerBuiltinResourcePack(
                    Identifier.of("fixedminecraft", "greentweaks"),
                    modContainer,
                    Text.of("Green Tweaks"),
                    ResourcePackActivationType.DEFAULT_ENABLED
            );
        });
    }

    public void renderCrosshair(DrawContext context,
                                @SuppressWarnings("unused") RenderTickCounter tickDelta) {
        MatrixStack matrices = context.getMatrices();
        matrices.push();
            MinecraftClient client = MinecraftClient.getInstance();

            ItemStack book = PlayerLookHelper.getLookingAtBook(null);
            if(book == ItemStack.EMPTY) return;

            List<Text> display = PlayerLookHelper.getBookText(book);
            for(int i = 0; i < display.size(); i++) {
                Text text = display.get(i);
                context.drawText(client.textRenderer, text, (int)(client.getWindow().getScaledWidth() / 2.0 - client.textRenderer.getWidth(text) / 2), (int)(client.getWindow().getScaledHeight() / 2.0 + 15 + (i * 10)), book.getItem() == Items.ENCHANTED_BOOK ? 16777045 : 16777215, true);
            }
        matrices.pop();
    }
    private static EquipmentModel createHumanoidAndHorseModel(String id) {
        return EquipmentModel.builder()
                .addHumanoidLayers(Identifier.ofVanilla(id))
                .addLayers(EquipmentModel.LayerType.HORSE_BODY, EquipmentModel.Layer.createWithLeatherColor(Identifier.ofVanilla(id), false))
                .build();
    }
}
