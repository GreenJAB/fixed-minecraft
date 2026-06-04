package net.greenjab.fixedminecraft.models;

import net.fabricmc.fabric.api.client.rendering.v1.ModelLayerRegistry;
import net.greenjab.fixedminecraft.FixedMinecraft;
import net.greenjab.fixedminecraft.registry.registries.ItemRegistry;
import net.minecraft.client.model.animal.equine.AbstractEquineModel;
import net.minecraft.client.model.animal.equine.DonkeyModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshTransformer;
import net.minecraft.client.model.object.boat.BoatModel;
import net.minecraft.client.renderer.entity.BoatRenderer;
import net.minecraft.client.renderer.entity.EntityRenderers;
import org.spongepowered.asm.mixin.Unique;

/** Credit: Viola-Siemens */
public class CustomModelLayers {
   public static final ModelLayerLocation VILLAGER_ARMOR_HEAD = register("villager", "outer_armor_head");
    public static final ModelLayerLocation VILLAGER_ARMOR_CHEST = register("villager", "outer_armor_chest");
    public static final ModelLayerLocation VILLAGER_ARMOR_LEGS = register("villager", "outer_armor_legs");
    public static final ModelLayerLocation VILLAGER_ARMOR_FEET = register("villager", "outer_armor_feet");

    public static ModelLayerLocation AZALEA_BOAT = register("boat/azalea", "azalea");
    public static ModelLayerLocation AZALEA_CHEST_BOAT = register("chest_boat/azalea", "azalea");

    public static ModelLayerLocation MULE_ARMOR = register("mule_armor", "main");


    private static ModelLayerLocation register(String path, String layer) {
        return new ModelLayerLocation(FixedMinecraft.id(path), layer);
    }

    public static void onRegisterLayers() {
        ModelLayerRegistry.registerModelLayer(VILLAGER_ARMOR_HEAD, () -> VillagerArmorModel.createBodyLayer(new CubeDeformation(0.5F)));
        ModelLayerRegistry.registerModelLayer(VILLAGER_ARMOR_CHEST, () -> VillagerArmorModel.createBodyLayer(new CubeDeformation(0.5F)));
        ModelLayerRegistry.registerModelLayer(VILLAGER_ARMOR_LEGS, () -> VillagerArmorModel.createBodyLayer(new CubeDeformation(0.0F)));
        ModelLayerRegistry.registerModelLayer(VILLAGER_ARMOR_FEET, () -> VillagerArmorModel.createBodyLayer(new CubeDeformation(0.5F)));
        ModelLayerRegistry.registerModelLayer(AZALEA_BOAT, BoatModel::createBoatModel);
        ModelLayerRegistry.registerModelLayer(AZALEA_CHEST_BOAT, BoatModel::createChestBoatModel);

        ModelLayerRegistry.registerModelLayer(MULE_ARMOR, CustomModelLayers::createMuleArmorModel);

        EntityRenderers.register(ItemRegistry.AZALEA_BOAT_ENTITY, context -> new BoatRenderer(context, CustomModelLayers.AZALEA_BOAT));
        EntityRenderers.register(ItemRegistry.AZALEA_CHEST_BOAT_ENTITY,context -> new BoatRenderer(context, CustomModelLayers.AZALEA_CHEST_BOAT));

    }

    @Unique
    private static LayerDefinition createMuleArmorModel() {
        return LayerDefinition.create(AbstractEquineModel.createBodyMesh(new CubeDeformation(0.1F)), 64, 64)
                .apply(DonkeyModel.DONKEY_TRANSFORMER)
                .apply(MeshTransformer.scaling(0.92F));
    }
}
