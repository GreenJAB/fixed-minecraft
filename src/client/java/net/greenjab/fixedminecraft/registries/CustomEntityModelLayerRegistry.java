package net.greenjab.fixedminecraft.registries;

import net.greenjab.fixedminecraft.FixedMinecraft;
import net.minecraft.client.model.geom.ModelLayerLocation;

public class CustomEntityModelLayerRegistry {

    public static final ModelLayerLocation DISPENSER_MINECART = new ModelLayerLocation(FixedMinecraft.id("dispenser_minecart"), "main");

    public static void registerEntityModelLayer() {
        System.out.println("register EntityModelLayer");
    }
}
