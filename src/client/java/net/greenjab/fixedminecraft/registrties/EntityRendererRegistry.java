package net.greenjab.fixedminecraft.registrties;

import net.greenjab.fixedminecraft.registry.registries.StatusRegistry;
import net.minecraft.client.render.entity.EntityRenderers;
import net.minecraft.client.render.entity.MinecartEntityRenderer;

public class EntityRendererRegistry {

    public static void registerEntityRenderer() {
        System.out.println("register EntityRenderer");

        EntityRenderers.register(StatusRegistry.DISPENCER_MINECART_ENTITY_TYPE, context -> new MinecartEntityRenderer(context, EntityModelLayerRegistry.DISPENSER_MINECART));
    }
}
