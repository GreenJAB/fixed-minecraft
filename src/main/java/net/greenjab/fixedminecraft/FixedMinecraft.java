package net.greenjab.fixedminecraft;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.greenjab.fixedminecraft.network.SyncHandler;
import net.greenjab.fixedminecraft.registry.item.map_book.MapBookState;
import net.greenjab.fixedminecraft.registry.item.map_book.MapBookStateManager;
import net.greenjab.fixedminecraft.registry.registries.BiomeAdditions;
import net.greenjab.fixedminecraft.registry.registries.BlockRegistry;
import net.greenjab.fixedminecraft.registry.registries.ItemGroupRegistry;
import net.greenjab.fixedminecraft.registry.registries.GameRuleRegistry;
import net.greenjab.fixedminecraft.registry.registries.LootTableAdditions;
import net.greenjab.fixedminecraft.registry.registries.MenuRegistry;
import net.greenjab.fixedminecraft.registry.registries.LootTableRegistry;
import net.greenjab.fixedminecraft.registry.registries.EntityTypeRegistry;
import net.greenjab.fixedminecraft.registry.registries.MapDecorationRegistry;
import net.greenjab.fixedminecraft.registry.registries.MemoryRegistry;
import net.greenjab.fixedminecraft.registry.registries.ParticleRegistry;
import net.greenjab.fixedminecraft.registry.registries.MobEffectRegistry;
import net.greenjab.fixedminecraft.registry.registries.TrimMaterialsRegistry;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DispenserBlock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;

public class FixedMinecraft implements ModInitializer {
    public static Logger logger = LoggerFactory.getLogger("FixedMinecraft");
    public static MinecraftServer SERVER = null;

    public static HashMap<Item, Integer> ItemCapacities = new HashMap<>();
    public static HashMap<Block, Block> corals = new HashMap<>();

    public static final String MOD_NAME = "Fixed Minecraft";
    public static final String NAMESPACE = "fixedminecraft";

    @Override public void onInitialize() {
        logger.info("Initializing " + MOD_NAME);

        SyncHandler.init();

        ItemGroupRegistry.register();
        GameRuleRegistry.registerGameRules();
        BlockRegistry.registerFireBlocks();
        LootTableRegistry.registerLootTable();
        MobEffectRegistry.registerMobEffects();
        ParticleRegistry.registerParticles();
        MapDecorationRegistry.registerMapDecorations();
        EntityTypeRegistry.registerEntityTypes();
        MemoryRegistry.registerMemories();
        MenuRegistry.registerMenus();
        TrimMaterialsRegistry.registerTrimMaterials();

        BiomeAdditions.registerBiomeAdds();
        LootTableAdditions.registerLootTableAdds();


        DispenserBlock.registerProjectileBehavior(Items.BRICK);
        DispenserBlock.registerProjectileBehavior(Items.NETHER_BRICK);
        DispenserBlock.registerProjectileBehavior(Items.RESIN_BRICK);
        DispenserBlock.registerProjectileBehavior(Items.TRIDENT);

        FabricLoader.getInstance().getModContainer(NAMESPACE).ifPresent(modContainer ->
                ResourceManagerHelper.registerBuiltinResourcePack(
                FixedMinecraft.id("tiered_crafting"),
                modContainer,
                Component.nullToEmpty("fixed.tiered_crafting"),
                ResourcePackActivationType.NORMAL
        ));

        FabricLoader.getInstance().getModContainer(NAMESPACE).ifPresent(modContainer ->
                ResourceManagerHelper.registerBuiltinResourcePack(
                FixedMinecraft.id("removed_features_21_11"),
                modContainer,
                Component.nullToEmpty("fixed.removed_1.21"),
                ResourcePackActivationType.DEFAULT_ENABLED
        ));


        CommandRegistrationCallback.EVENT.register((dispatcher, _, _) ->
                dispatcher.register(Commands.literal("mapBookMarker")
                        .then(Commands.argument("id", IntegerArgumentType.integer())
                                .then(Commands.argument("x", StringArgumentType.string())
                                        .then(Commands.argument("z", StringArgumentType.string())
                                                .then(Commands.argument("dim", StringArgumentType.string())
                                                        .executes(FixedMinecraft::executeMapBookMarker)))))));
    }

    private static int executeMapBookMarker(CommandContext<CommandSourceStack> context) {
        int id = IntegerArgumentType.getInteger(context, "id");
        double x = Double.parseDouble(StringArgumentType.getString(context, "x"));
        double z = Double.parseDouble(StringArgumentType.getString(context, "z"));
        String dim = StringArgumentType.getString(context, "dim");
        MapBookState mapBookState = MapBookStateManager.INSTANCE.getMapBookState(SERVER, id);
        if (mapBookState!=null) mapBookState.setMarker(x, z, dim);
        return 1;
    }

    public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(NAMESPACE, path);
    }

    public static ArrayList<ItemStack> getArmor(LivingEntity entity) {
        ArrayList<ItemStack> armor = new ArrayList<>();
        armor.add(entity.getItemBySlot(EquipmentSlot.FEET));
        armor.add(entity.getItemBySlot(EquipmentSlot.LEGS));
        armor.add(entity.getItemBySlot(EquipmentSlot.CHEST));
        armor.add(entity.getItemBySlot(EquipmentSlot.HEAD));
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

//TODO mansion buff???
