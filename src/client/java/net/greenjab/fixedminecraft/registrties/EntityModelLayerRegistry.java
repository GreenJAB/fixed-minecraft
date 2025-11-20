package net.greenjab.fixedminecraft.registrties;

import net.greenjab.fixedminecraft.FixedMinecraft;
import net.minecraft.client.render.entity.model.EntityModelLayer;

public class EntityModelLayerRegistry {

    public static final EntityModelLayer DISPENSER_MINECART = new EntityModelLayer(FixedMinecraft.id("dispenser_minecart"), "main");

    public static void registerEntityModelLayer() {
        System.out.println("register EntityModelLayer");
    }
}
