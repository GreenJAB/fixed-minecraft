package net.greenjab.fixedminecraft.registrties;

import net.greenjab.fixedminecraft.registry.registries.OtherRegistry;
import net.minecraft.client.render.entity.EntityRendererFactories;
import net.minecraft.client.render.entity.MinecartEntityRenderer;

public class EntityRendererRegistry {

    public static void registerEntityRenderer() {
        System.out.println("register EntityRenderer");

        EntityRendererFactories.register(OtherRegistry.DISPENCER_MINECART_ENTITY_TYPE, context -> new MinecartEntityRenderer(context, EntityModelLayerRegistry.DISPENSER_MINECART));
    }
}
