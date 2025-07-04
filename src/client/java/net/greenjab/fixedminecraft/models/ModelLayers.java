package net.greenjab.fixedminecraft.models;


import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.greenjab.fixedminecraft.FixedMinecraft;
import net.greenjab.fixedminecraft.registry.registries.ItemRegistry;
import net.minecraft.client.model.Dilation;
import net.minecraft.client.render.entity.BoatEntityRenderer;
import net.minecraft.client.render.entity.model.BoatEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.util.Identifier;

/** Credit: Viola-Siemens */
public class ModelLayers {
    public static final EntityModelLayer VILLAGER_INNER_ARMOR = register("villager", "inner_armor");
    public static final EntityModelLayer VILLAGER_OUTER_ARMOR = register("villager", "outer_armor");

    public static EntityModelLayer AZALEA_BOAT = register("boat/azalea", "azalea");
    public static EntityModelLayer AZALEA_CHEST_BOAT = register("chest_boat/azalea", "azalea");


    private static EntityModelLayer register(String path, String layer) {
        return new EntityModelLayer(FixedMinecraft.id(path), layer);
    }

    public static void onRegisterLayers() {
        EntityModelLayerRegistry.registerModelLayer(VILLAGER_INNER_ARMOR, () -> VillagerArmorModel.createBodyLayer(new Dilation(0.0F), 0.0F, 0.25F));
        EntityModelLayerRegistry.registerModelLayer(VILLAGER_OUTER_ARMOR, () -> VillagerArmorModel.createBodyLayer(new Dilation(1.0F), 0.0F, -0.5F));

        EntityModelLayerRegistry.registerModelLayer(AZALEA_BOAT, BoatEntityModel::getTexturedModelData);
        EntityModelLayerRegistry.registerModelLayer(AZALEA_CHEST_BOAT, BoatEntityModel::getChestTexturedModelData);

        EntityRendererRegistry.register(ItemRegistry.AZALEA_BOAT_ENTITY, context -> new BoatEntityRenderer(context, ModelLayers.AZALEA_BOAT));
        EntityRendererRegistry.register(ItemRegistry.AZALEA_CHEST_BOAT_ENTITY,context -> new BoatEntityRenderer(context, ModelLayers.AZALEA_CHEST_BOAT));

    }
}
