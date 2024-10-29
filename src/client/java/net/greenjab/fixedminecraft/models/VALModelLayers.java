package net.greenjab.fixedminecraft.models;


import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.minecraft.client.model.Dilation;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.util.Identifier;

public class VALModelLayers {
    public static final EntityModelLayer VILLAGER_INNER_ARMOR = registerInnerArmor("villager");
    public static final EntityModelLayer VILLAGER_OUTER_ARMOR = registerOuterArmor("villager");

    private static EntityModelLayer registerInnerArmor(String name) {
        return register(name, "inner_armor");
    }

    private static EntityModelLayer registerOuterArmor(String name) {
        return register(name, "outer_armor");
    }

    private static EntityModelLayer register(String name, String layer) {
        return new EntityModelLayer(new Identifier("fixedminecraft", name), layer);
    }

    public static void onRegisterLayers() {
        EntityModelLayerRegistry.registerModelLayer(VILLAGER_INNER_ARMOR, () -> VillagerArmorModel.createBodyLayer(new Dilation(0.0F), 0.0F, 0.25F));
        EntityModelLayerRegistry.registerModelLayer(VILLAGER_OUTER_ARMOR, () -> VillagerArmorModel.createBodyLayer(new Dilation(1.0F), 0.0F, -0.5F));
    }
}
