package net.greenjab.fixedminecraft;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.greenjab.fixedminecraft.network.SyncHandler;
import net.greenjab.fixedminecraft.registry.registries.ItemGroupRegistry;
import net.greenjab.fixedminecraft.registry.registries.GameruleRegistry;
import net.greenjab.fixedminecraft.registry.registries.RecipeRegistry;
import net.minecraft.block.DispenserBlock;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.ArrayList;
import java.util.HashMap;

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
    }

    public static Identifier id(String path) {
        return Identifier.of(NAMESPACE, path);
    }

    public static ArrayList<ItemStack> getArmor(LivingEntity entity) {
        ArrayList<ItemStack> armor = new ArrayList<>();
        armor.add(entity.getEquippedStack(EquipmentSlot.FEET));
        armor.add(entity.getEquippedStack(EquipmentSlot.LEGS));
        armor.add(entity.getEquippedStack(EquipmentSlot.CHEST));
        armor.add(entity.getEquippedStack(EquipmentSlot.HEAD));
        return armor;
    }

    public static ArrayList<ItemStack> getArmorBypass(LivingEntity entity) {
        ArrayList<ItemStack> armor = new ArrayList<>();
        armor.add(entity.equipment.get(EquipmentSlot.FEET));
        armor.add(entity.equipment.get(EquipmentSlot.LEGS));
        armor.add(entity.equipment.get(EquipmentSlot.CHEST));
        armor.add(entity.equipment.get(EquipmentSlot.HEAD));
        return armor;
    }
}
