package net.greenjab.fixedminecraft.models;


import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.greenjab.fixedminecraft.FixedMinecraft;
import net.greenjab.fixedminecraft.registry.registries.ItemRegistry;
import net.minecraft.client.model.Dilation;
import net.minecraft.client.render.entity.BoatEntityRenderer;
import net.minecraft.client.render.entity.model.BoatEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.EquipmentModelData;

/** Credit: Viola-Siemens */
public class ModelLayers {
   // public static final EquipmentModelData<EntityModelLayer> VILLAGER_ARMOR = registerEquipment("villager_armor");
    //public static final EntityModelLayer VILLAGER_INNER_ARMOR = register("villager", "inner_armor");
    public static final EntityModelLayer VILLAGER_ARMOR_HEAD = register("villager", "outer_armor_head");
    public static final EntityModelLayer VILLAGER_ARMOR_CHEST = register("villager", "outer_armor_chest");
    public static final EntityModelLayer VILLAGER_ARMOR_LEGS = register("villager", "outer_armor_legs");
    public static final EntityModelLayer VILLAGER_ARMOR_FEET = register("villager", "outer_armor_feet");

    public static EntityModelLayer AZALEA_BOAT = register("boat/azalea", "azalea");
    public static EntityModelLayer AZALEA_CHEST_BOAT = register("chest_boat/azalea", "azalea");


    private static EntityModelLayer register(String path, String layer) {
        return new EntityModelLayer(FixedMinecraft.id(path), layer);
    }

    private static EquipmentModelData<EntityModelLayer> registerEquipment(String id) {
        return new EquipmentModelData<>(register(id, "helmet"), register(id, "chestplate"), register(id, "leggings"), register(id, "boots"));
    }

    public static void onRegisterLayers() {
        //EntityModelLayerRegistry.registerModelLayer(VILLAGER_INNER_ARMOR, () -> VillagerArmorModel.createBodyLayer(new Dilation(0.0F)));
        EntityModelLayerRegistry.registerModelLayer(VILLAGER_ARMOR_HEAD, () -> VillagerArmorModel.createBodyLayer(new Dilation(0.5F)));
        EntityModelLayerRegistry.registerModelLayer(VILLAGER_ARMOR_CHEST, () -> VillagerArmorModel.createBodyLayer(new Dilation(0.5F)));
        EntityModelLayerRegistry.registerModelLayer(VILLAGER_ARMOR_LEGS, () -> VillagerArmorModel.createBodyLayer(new Dilation(0.0F)));
        EntityModelLayerRegistry.registerModelLayer(VILLAGER_ARMOR_FEET, () -> VillagerArmorModel.createBodyLayer(new Dilation(0.5F)));
        EntityModelLayerRegistry.registerModelLayer(AZALEA_BOAT, BoatEntityModel::getTexturedModelData);
        EntityModelLayerRegistry.registerModelLayer(AZALEA_CHEST_BOAT, BoatEntityModel::getChestTexturedModelData);

        EntityRendererRegistry.register(ItemRegistry.AZALEA_BOAT_ENTITY, context -> new BoatEntityRenderer(context, ModelLayers.AZALEA_BOAT));
        EntityRendererRegistry.register(ItemRegistry.AZALEA_CHEST_BOAT_ENTITY,context -> new BoatEntityRenderer(context, ModelLayers.AZALEA_CHEST_BOAT));

    }
}
