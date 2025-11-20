package net.greenjab.fixedminecraft.registry.registries;

import net.greenjab.fixedminecraft.FixedMinecraft;
import net.greenjab.fixedminecraft.registry.item.PatinaItem;
import net.greenjab.fixedminecraft.registry.item.map_book.MapBookAdditionsComponent;
import net.greenjab.fixedminecraft.registry.item.map_book.MapBookItem;
import net.greenjab.fixedminecraft.registry.item.EchoFruitItem;
import net.greenjab.fixedminecraft.registry.item.TotemItem;
import net.greenjab.fixedminecraft.registry.other.BaitComponent;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.component.ComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ConsumableComponent;
import net.minecraft.component.type.DeathProtectionComponent;
import net.minecraft.component.type.FireworksComponent;
import net.minecraft.component.type.FoodComponents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.entity.vehicle.ChestBoatEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.BoatItem;
import net.minecraft.item.FireworkRocketItem;
import net.minecraft.item.HangingSignItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.item.MinecartItem;
import net.minecraft.item.SignItem;
import net.minecraft.item.TallBlockItem;
import net.minecraft.item.consume.ApplyEffectsConsumeEffect;
import net.minecraft.item.consume.UseAction;
import net.minecraft.item.equipment.ArmorMaterials;
import net.minecraft.potion.Potion;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public class ItemRegistry {

    public static final Item DRAGON_FIREWORK_ROCKET = register(
            "dragon_firework_rocket", FireworkRocketItem::new, new Item.Settings().useCooldown(1.0F).component(DataComponentTypes.FIREWORKS, new FireworksComponent(1, List.of()))
    );
    public static final Item MAP_BOOK = register("map_book", MapBookItem::new, new Item.Settings().maxCount(16));
    public static final ComponentType<MapBookAdditionsComponent> MAP_BOOK_ADDITIONS = registerComponent("map_book_additions", (builder) -> builder.codec(MapBookAdditionsComponent.CODEC).packetCodec(MapBookAdditionsComponent.PACKET_CODEC).cache());

    public static final Item CHAINMAIL_HORSE_ARMOR = register(
            "chainmail_horse_armor", new Item.Settings().horseArmor(ArmorMaterials.CHAIN));


    public static final Item NETHERITE_HORSE_ARMOR = register(
            "netherite_horse_armor",new Item.Settings().horseArmor(ArmorMaterials.NETHERITE).fireproof().rarity(Rarity.RARE));

    public static final Item BROKEN_TOTEM = register("broken_totem", Item::new, new Item.Settings().maxCount(1).rarity(Rarity.UNCOMMON));
    public static final Item ECHO_TOTEM = register(
            "echo_totem", TotemItem::new, new Item.Settings().maxCount(1).rarity(Rarity.UNCOMMON).component(DataComponentTypes.DEATH_PROTECTION, DeathProtectionComponent.TOTEM_OF_UNDYING));
    public static final Item ECHO_FRUIT = register(
            "echo_fruit", EchoFruitItem::new, new Item.Settings().maxCount(1).rarity(Rarity.UNCOMMON).food(FoodComponents.CHORUS_FRUIT));

    public static final Item NETHERITE_ANVIL = register(BlockRegistry.NETHERITE_ANVIL, new Item.Settings().fireproof());
    public static final Item CHIPPED_NETHERITE_ANVIL = register(BlockRegistry.CHIPPED_NETHERITE_ANVIL, new Item.Settings().fireproof());
    public static final Item DAMAGED_NETHERITE_ANVIL = register(BlockRegistry.DAMAGED_NETHERITE_ANVIL, new Item.Settings().fireproof());

    public static final Item COPPER_RAIL = register(BlockRegistry.COPPER_RAIL);
    public static final Item EXPOSED_COPPER_RAIL = register(BlockRegistry.EXPOSED_COPPER_RAIL);
    public static final Item WEATHERED_COPPER_RAIL = register(BlockRegistry.WEATHERED_COPPER_RAIL);
    public static final Item OXIDIZED_COPPER_RAIL = register(BlockRegistry.OXIDIZED_COPPER_RAIL);

    public static final Item WAXED_COPPER_RAIL = register(BlockRegistry.WAXED_COPPER_RAIL);
    public static final Item WAXED_EXPOSED_COPPER_RAIL = register(BlockRegistry.WAXED_EXPOSED_COPPER_RAIL);
    public static final Item WAXED_WEATHERED_COPPER_RAIL = register(BlockRegistry.WAXED_WEATHERED_COPPER_RAIL);
    public static final Item WAXED_OXIDIZED_COPPER_RAIL = register(BlockRegistry.WAXED_OXIDIZED_COPPER_RAIL);


    public static final Item PATINA = register("patina", PatinaItem::new, new Item.Settings());
    public static final Item REDSTONE_LANTERN = register(BlockRegistry.REDSOTNE_LANTERN);

    public static final Item DISPENSER_MINECART = register(
            "dispenser_minecart", settings -> new MinecartItem(StatusRegistry.DISPENCER_MINECART_ENTITY_TYPE, settings), new Item.Settings().maxCount(1)
    );

    public static final ConsumableComponent GLOW_BERRIES_EFFECT = food()
            .consumeEffect(new ApplyEffectsConsumeEffect(new StatusEffectInstance(StatusEffects.GLOWING, 200, 0), 1F))
            .build();

    public static ConsumableComponent.Builder food() {
        return ConsumableComponent.builder().consumeSeconds(1.6F).useAction(UseAction.EAT).sound(SoundEvents.ENTITY_GENERIC_EAT).consumeParticles(true);
    }

    /** This is used, IntelliJ just doesn't realise */
    public static final RegistryEntry<Potion> BLINDNESS = register("blindness", new Potion("blindness", new StatusEffectInstance(StatusEffects.BLINDNESS, 800)));
    public static final RegistryEntry<Potion> LEVITATION = register("levitation", new Potion("levitation", new StatusEffectInstance(StatusEffects.LEVITATION, 1200)));

    public static final ComponentType<BaitComponent> BAIT_POWER = registerComponent("bait_power", (builder) -> builder.codec(BaitComponent.CODEC).packetCodec(BaitComponent.PACKET_CODEC).cache());

    public static Item register(String id, Item.Settings settings) {
        return register(keyOf(id), Item::new, settings);
    }
    public static Item register(String id, Function<Item.Settings, Item> factory, Item.Settings settings) {
        return register(keyOf(id), factory, settings);
    }
    private static RegistryKey<Item> keyOf(String id) {
        return RegistryKey.of(RegistryKeys.ITEM, FixedMinecraft.id(id));
    }
    public static Item register(RegistryKey<Item> key, Function<Item.Settings, Item> factory, Item.Settings settings) {
        Item item = factory.apply(settings.registryKey(key));
        if (item instanceof BlockItem blockItem) {
            blockItem.appendBlocks(Item.BLOCK_ITEMS, item);
        }

        return Registry.register(Registries.ITEM, key, item);
    }
    public static Item register(Block block) {
        return register(block, BlockItem::new, new Item.Settings());
    }
    public static Item register(Block block, Item.Settings settings) {
        return register(block, BlockItem::new, settings);
    }
    public static Item register(Block block, BiFunction<Block, Item.Settings, Item> factory) {
        return register(block, factory, new Item.Settings());
    }
    public static Item register(Block block, BiFunction<Block, Item.Settings, Item> factory, Item.Settings settings) {
        return register(
                keyOf(block.getRegistryEntry().registryKey()),
                itemSettings -> factory.apply(block, itemSettings),
                settings.useBlockPrefixedTranslationKey()
        );
    }
    private static RegistryKey<Item> keyOf(RegistryKey<Block> blockKey) {
        return RegistryKey.of(RegistryKeys.ITEM, blockKey.getValue());
    }

    private static <T> ComponentType<T> registerComponent(String id, UnaryOperator<ComponentType.Builder<T>> builderOperator) {
        return Registry.register(Registries.DATA_COMPONENT_TYPE, id, builderOperator.apply(ComponentType.builder()).build());
    }

    private static RegistryEntry<Potion> register(String name, Potion potion) {
        return Registry.registerReference(Registries.POTION, FixedMinecraft.id(name), potion);
    }

    public static final Item AZALEA_PLANKS = register(BlockRegistry.AZALEA_PLANKS);
    public static final Item AZALEA_LOG = register(BlockRegistry.AZALEA_LOG);
    public static final Item STRIPPED_AZALEA_LOG = register(BlockRegistry.STRIPPED_AZALEA_LOG);
    public static final Item STRIPPED_AZALEA_WOOD = register(BlockRegistry.STRIPPED_AZALEA_WOOD);
    public static final Item AZALEA_WOOD = register(BlockRegistry.AZALEA_WOOD);
    public static final Item AZALEA_SLAB = register(BlockRegistry.AZALEA_SLAB);
    public static final Item AZALEA_FENCE = register(BlockRegistry.AZALEA_FENCE);
    public static final Item AZALEA_STAIRS = register(BlockRegistry.AZALEA_STAIRS);
    public static final Item AZALEA_BUTTON = register(BlockRegistry.AZALEA_BUTTON);
    public static final Item AZALEA_PRESSURE_PLATE = register(BlockRegistry.AZALEA_PRESSURE_PLATE);
    public static final Item AZALEA_DOOR = register(BlockRegistry.AZALEA_DOOR, TallBlockItem::new);
    public static final Item AZALEA_TRAPDOOR = register(BlockRegistry.AZALEA_TRAPDOOR);
    public static final Item AZALEA_FENCE_GATE = register(BlockRegistry.AZALEA_FENCE_GATE);

    public static final Item AZALEA_SIGN = register(
            BlockRegistry.AZALEA_SIGN, /* method_63727 */ (block, settings) -> new SignItem(block, BlockRegistry.AZALEA_WALL_SIGN, settings), new Item.Settings().maxCount(16)
    );
    public static final Item AZALEA_HANGING_SIGN = register(
            BlockRegistry.AZALEA_HANGING_SIGN,
            /* method_63705 */ (block, settings) -> new HangingSignItem(block, BlockRegistry.AZALEA_WALL_HANGING_SIGN, settings),
            new Item.Settings().maxCount(16)
    );

    public static final EntityType<BoatEntity> AZALEA_BOAT_ENTITY = register2(
            "azalea_boat",
            EntityType.Builder.create(getBoatFactory(/* method_64431 */ () -> ItemRegistry.AZALEA_BOAT), SpawnGroup.MISC)
                    .dropsNothing()
                    .dimensions(1.375F, 0.5625F)
                    .eyeHeight(0.5625F)
                    .maxTrackingRange(10)
    );
    public static final EntityType<ChestBoatEntity> AZALEA_CHEST_BOAT_ENTITY = register2(
            "azalea_chest_boat",
            EntityType.Builder.create(getChestBoatFactory(/* method_64430 */ () -> ItemRegistry.AZALEA_CHEST_BOAT), SpawnGroup.MISC)
                    .dropsNothing()
                    .dimensions(1.375F, 0.5625F)
                    .eyeHeight(0.5625F)
                    .maxTrackingRange(10)
    );

    public static final Item AZALEA_BOAT = register(
            "azalea_boat", /* method_63883 */ settings -> new BoatItem(AZALEA_BOAT_ENTITY, settings), new Item.Settings().maxCount(1)
    );
    public static final Item AZALEA_CHEST_BOAT = register(
            "azalea_chest_boat", /* method_63882 */ settings -> new BoatItem(AZALEA_CHEST_BOAT_ENTITY, settings), new Item.Settings().maxCount(1)
    );

    private static EntityType.EntityFactory<BoatEntity> getBoatFactory(Supplier<Item> itemSupplier) {
        return /* method_64439 */ (type, world) -> new BoatEntity(type, world, itemSupplier);
    }
    private static EntityType.EntityFactory<ChestBoatEntity> getChestBoatFactory(Supplier<Item> itemSupplier) {
        return /* method_64437 */ (type, world) -> new ChestBoatEntity(type, world, itemSupplier);
    }
    private static <T extends Entity> EntityType<T> register2(String id, EntityType.Builder<T> type) {
        return register2(keyOf2(id), type);
    }
    private static RegistryKey<EntityType<?>> keyOf2(String id) {
        return RegistryKey.of(RegistryKeys.ENTITY_TYPE, FixedMinecraft.id(id));
    }
    private static <T extends Entity> EntityType<T> register2(RegistryKey<EntityType<?>> key, EntityType.Builder<T> type) {
        return Registry.register(Registries.ENTITY_TYPE, key, type.build(key));
    }
}
