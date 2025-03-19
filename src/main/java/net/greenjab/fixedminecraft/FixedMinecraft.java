package net.greenjab.fixedminecraft;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.fabricmc.loader.api.FabricLoader;
import net.greenjab.fixedminecraft.network.SyncHandler;
import net.greenjab.fixedminecraft.registry.registries.ItemGroupRegistry;
import net.greenjab.fixedminecraft.registry.registries.GameruleRegistry;
import net.greenjab.fixedminecraft.registry.registries.RecipeRegistry;
import net.minecraft.block.DispenserBlock;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class FixedMinecraft implements ModInitializer {
    public static Logger logger = LoggerFactory.getLogger("FixedMinecraft");
    public static MinecraftServer SERVER = null;

    public static boolean netheriteAnvil = false;
    public static HashMap<Item, Integer> ItemCapacities = new HashMap<>();

    public static final String MOD_NAME = "Fixed Minecraft";
    public static final String NAMESPACE = "fixedminecraft";

    @Override public void onInitialize() {
        logger.info("Initializing " + MOD_NAME);

        SyncHandler.init();

        ItemGroupRegistry.register();

        RecipeRegistry.register();
        GameruleRegistry.register();

        DispenserBlock.registerProjectileBehavior(Items.BRICK);
        DispenserBlock.registerProjectileBehavior(Items.NETHER_BRICK);
        DispenserBlock.registerProjectileBehavior(Items.RESIN_BRICK);
        DispenserBlock.registerProjectileBehavior(Items.TRIDENT);

        FabricLoader.getInstance().getModContainer("fixedminecraft").ifPresent(modContainer -> {
            ResourceManagerHelper.registerBuiltinResourcePack(
                            Identifier.of("fixedminecraft", "tiered_crafting"),
                            modContainer,
                            Text.of("Tiered Crafting"),
                            ResourcePackActivationType.NORMAL
            );
        });


        /*FabricLoader.getInstance().getModContainer("fixedminecraft").ifPresent(modContainer -> {
            ResourceManagerHelper.registerBuiltinResourcePack(
                    Identifier.of("fixedminecraft", "greentweaks"),
                    modContainer,
                    Text.of("Green Tweaks"),
                    ResourcePackActivationType.DEFAULT_ENABLED
            );


            ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new SimpleSynchronousResourceReloadListener() {
                @Override
                public Identifier getFabricId() {
                    return Identifier.of("fixedminecraft", "greentweaks");
                }

                @Override
                public void reload(ResourceManager manager) {
                    // Clear Caches Here

                    for(Map.Entry<Identifier, Resource> resourceEntry : manager.findResources("resources/datapacks", path -> path.toString().endsWith(".json")).entrySet()) {
                        try(InputStream stream = manager.getResource(resourceEntry.getKey()).get().getInputStream()) {
                            // Consume the stream however you want, medium, rare, or well done.
                        } catch(Exception e) {
                            logger.error("Error occurred while loading resource json" + resourceEntry.toString(), e);
                        }
                    }
                }
            });

        });*/
    }

    public static Identifier id(String path) {
        return Identifier.of(NAMESPACE, path);
    }


}
