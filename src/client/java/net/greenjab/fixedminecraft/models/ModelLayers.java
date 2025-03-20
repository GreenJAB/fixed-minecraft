package net.greenjab.fixedminecraft.models;


import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.minecraft.client.model.Dilation;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.util.Identifier;

/** Credit: Viola-Siemens */
public class ModelLayers {
    public static final EntityModelLayer VILLAGER_INNER_ARMOR = registerInnerArmor();
    public static final EntityModelLayer VILLAGER_OUTER_ARMOR = registerOuterArmor();

    private static EntityModelLayer registerInnerArmor() {
        return register("inner_armor");
    }

    private static EntityModelLayer registerOuterArmor() {
        return register("outer_armor");
    }

    private static EntityModelLayer register(String layer) {
        return new EntityModelLayer(Identifier.of("fixedminecraft", "villager"), layer);
    }

    public static void onRegisterLayers() {
        EntityModelLayerRegistry.registerModelLayer(VILLAGER_INNER_ARMOR, () -> VillagerArmorModel.createBodyLayer(new Dilation(0.0F), 0.0F, 0.25F));
        EntityModelLayerRegistry.registerModelLayer(VILLAGER_OUTER_ARMOR, () -> VillagerArmorModel.createBodyLayer(new Dilation(1.0F), 0.0F, -0.5F));
    }
}
