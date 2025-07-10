package net.greenjab.fixedminecraft.models;


import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.greenjab.fixedminecraft.FixedMinecraft;
import net.greenjab.fixedminecraft.registry.registries.ItemRegistry;
import net.minecraft.client.model.Dilation;
import net.minecraft.client.render.entity.BoatEntityRenderer;
import net.minecraft.client.render.entity.model.BoatEntityModel;
import net.minecraft.client.render.entity.model.ChestBoatEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayer;

/** Credit: Viola-Siemens */
public class ModelLayers {

    public static EntityModelLayer AZALEA_BOAT = register("boat/azalea", "azalea");
    public static EntityModelLayer AZALEA_CHEST_BOAT = register("chest_boat/azalea", "azalea");


    private static EntityModelLayer register(String path, String layer) {
        return new EntityModelLayer(FixedMinecraft.id(path), layer);
    }

    public static void onRegisterLayers() {
        EntityModelLayerRegistry.registerModelLayer(AZALEA_BOAT, BoatEntityModel::getTexturedModelData);
        EntityModelLayerRegistry.registerModelLayer(AZALEA_CHEST_BOAT, ChestBoatEntityModel::getTexturedModelData);

    }
}
