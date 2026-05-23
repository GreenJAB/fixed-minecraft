package net.greenjab.fixedminecraft.registry.registries;

import net.greenjab.fixedminecraft.FixedMinecraft;
import net.greenjab.fixedminecraft.registry.item.PatinaItem;
import net.greenjab.fixedminecraft.registry.item.map_book.MapBookAdditionsComponent;
import net.greenjab.fixedminecraft.registry.item.map_book.MapBookItem;
import net.greenjab.fixedminecraft.registry.item.EchoFruitItem;
import net.greenjab.fixedminecraft.registry.item.NewTotemItem;
import net.greenjab.fixedminecraft.registry.other.BaitComponent;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.vehicle.boat.Boat;
import net.minecraft.world.entity.vehicle.boat.ChestBoat;
import net.minecraft.world.food.Foods;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.BoatItem;
import net.minecraft.world.item.DoubleHighBlockItem;
import net.minecraft.world.item.FireworkRocketItem;
import net.minecraft.world.item.HangingSignItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.MinecartItem;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.SignItem;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.item.component.DeathProtection;
import net.minecraft.world.item.component.Fireworks;
import net.minecraft.world.item.consume_effects.ApplyStatusEffectsConsumeEffect;
import net.minecraft.world.item.equipment.ArmorMaterials;
import net.minecraft.world.level.block.Block;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public class ItemRegistry {

    public static final Item DRAGON_FIREWORK_ROCKET = register(
            "dragon_firework_rocket", FireworkRocketItem::new, new Item.Properties().useCooldown(1.0F)
                    .component(DataComponents.FIREWORKS, new Fireworks(1, List.of()))
    );
    public static final Item MAP_BOOK = register("map_book", MapBookItem::new, new Item.Properties().stacksTo(16));
    public static final DataComponentType<MapBookAdditionsComponent> MAP_BOOK_ADDITIONS = registerComponent("map_book_additions", (builder) -> builder.persistent(MapBookAdditionsComponent.CODEC)
            .networkSynchronized(MapBookAdditionsComponent.PACKET_CODEC)
            .cacheEncoding());

    public static final Item CHAINMAIL_HORSE_ARMOR = register(
            "chainmail_horse_armor", new Item.Properties().horseArmor(ArmorMaterials.CHAINMAIL));

    public static final Item BROKEN_TOTEM = register("broken_totem", Item::new, new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON));
    public static final Item ECHO_TOTEM = register(
            "echo_totem", NewTotemItem::new, new Item.Properties().stacksTo(1)
                    .rarity(Rarity.UNCOMMON)
                    .component(DataComponents.DEATH_PROTECTION, DeathProtection.TOTEM_OF_UNDYING));
    public static final Item ECHO_FRUIT = register(
            "echo_fruit", EchoFruitItem::new, new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON).food(Foods.CHORUS_FRUIT));

    public static final Item NETHERITE_ANVIL = register(BlockRegistry.NETHERITE_ANVIL, new Item.Properties().fireResistant());
    public static final Item CHIPPED_NETHERITE_ANVIL = register(BlockRegistry.CHIPPED_NETHERITE_ANVIL, new Item.Properties().fireResistant());
    public static final Item DAMAGED_NETHERITE_ANVIL = register(BlockRegistry.DAMAGED_NETHERITE_ANVIL, new Item.Properties().fireResistant());

    public static final Item COPPER_RAIL = register(BlockRegistry.COPPER_RAIL);
    public static final Item EXPOSED_COPPER_RAIL = register(BlockRegistry.EXPOSED_COPPER_RAIL);
    public static final Item WEATHERED_COPPER_RAIL = register(BlockRegistry.WEATHERED_COPPER_RAIL);
    public static final Item OXIDIZED_COPPER_RAIL = register(BlockRegistry.OXIDIZED_COPPER_RAIL);

    public static final Item WAXED_COPPER_RAIL = register(BlockRegistry.WAXED_COPPER_RAIL);
    public static final Item WAXED_EXPOSED_COPPER_RAIL = register(BlockRegistry.WAXED_EXPOSED_COPPER_RAIL);
    public static final Item WAXED_WEATHERED_COPPER_RAIL = register(BlockRegistry.WAXED_WEATHERED_COPPER_RAIL);
    public static final Item WAXED_OXIDIZED_COPPER_RAIL = register(BlockRegistry.WAXED_OXIDIZED_COPPER_RAIL);

    public static final Item PATINA = register("patina", PatinaItem::new, new Item.Properties());
    public static final Item REDSTONE_LANTERN = register(BlockRegistry.REDSOTNE_LANTERN);

    public static final Item DISPENSER_MINECART = register(
            "dispenser_minecart", settings -> new MinecartItem(EntityTypeRegistry.DISPENCER_MINECART_ENTITY_TYPE, settings), new Item.Properties().stacksTo(1)
    );

    public static final Item SPEAR = register(
            "spear", new Item.Properties().rarity(Rarity.EPIC).spear(ToolMaterial.DIAMOND, 1.05F, 1.075F, 0.5F, 3.0F, 7.5F, 4.0F, 5.1F, 10.0F, 4.6F)
    );
    public static final Item NAUTILUS_ARMOR = register("nautilus_armor", new Item.Properties().nautilusArmor(ArmorMaterials.ARMADILLO_SCUTE));



    public static final Consumable GLOW_BERRIES_EFFECT = food()
            .onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(MobEffects.GLOWING, 200, 0), 1F))
            .build();

    public static Consumable.Builder food() {
        return Consumable.builder().consumeSeconds(1.6F).animation(ItemUseAnimation.EAT).sound(SoundEvents.GENERIC_EAT).hasConsumeParticles(true);
    }

    /** This is used, IntelliJ just doesn't realise */
    public static final Holder<Potion> BLINDNESS = register("blindness", new Potion("blindness", new MobEffectInstance(MobEffects.BLINDNESS, 800)));
    public static final Holder<Potion> LEVITATION = register("levitation", new Potion("levitation", new MobEffectInstance(MobEffects.LEVITATION, 1200)));

    public static final DataComponentType<BaitComponent> BAIT_POWER = registerComponent("bait_power", (builder) -> builder.persistent(BaitComponent.CODEC).networkSynchronized(BaitComponent.PACKET_CODEC).cacheEncoding());

    public static Item register(String id, Item.Properties settings) {
        return register(keyOf(id), Item::new, settings);
    }
    public static Item register(String id, Function<Item.Properties, Item> factory, Item.Properties settings) {
        return register(keyOf(id), factory, settings);
    }
    private static ResourceKey<Item> keyOf(String id) {
        return ResourceKey.create(Registries.ITEM, FixedMinecraft.id(id));
    }
    public static Item register(ResourceKey<Item> key, Function<Item.Properties, Item> factory, Item.Properties settings) {
        Item item = factory.apply(settings.setId(key));
        if (item instanceof BlockItem blockItem) {
            blockItem.registerBlocks(Item.BY_BLOCK, item);
        }

        return Registry.register(BuiltInRegistries.ITEM, key, item);
    }
    public static Item register(Block block) {
        return register(block, BlockItem::new, new Item.Properties());
    }
    public static Item register(Block block, Item.Properties settings) {
        return register(block, BlockItem::new, settings);
    }
    public static Item register(Block block, BiFunction<Block, Item.Properties, Item> factory) {
        return register(block, factory, new Item.Properties());
    }
    public static Item register(Block block, BiFunction<Block, Item.Properties, Item> factory, Item.Properties settings) {
        return register(
                keyOf(block.builtInRegistryHolder().key()),
                itemSettings -> factory.apply(block, itemSettings),
                settings.useBlockDescriptionPrefix()
        );
    }
    private static ResourceKey<Item> keyOf(ResourceKey<Block> blockKey) {
        return ResourceKey.create(Registries.ITEM, blockKey.identifier());
    }

    private static <T> DataComponentType<T> registerComponent(String id, UnaryOperator<DataComponentType.Builder<T>> builderOperator) {
        return Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, id, builderOperator.apply(DataComponentType.builder()).build());
    }

    private static Holder<Potion> register(String name, Potion potion) {
        return Registry.registerForHolder(BuiltInRegistries.POTION, FixedMinecraft.id(name), potion);
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
    public static final Item AZALEA_DOOR = register(BlockRegistry.AZALEA_DOOR, DoubleHighBlockItem::new);
    public static final Item AZALEA_TRAPDOOR = register(BlockRegistry.AZALEA_TRAPDOOR);
    public static final Item AZALEA_FENCE_GATE = register(BlockRegistry.AZALEA_FENCE_GATE);
    public static final Item AZALEA_SHELF = register(BlockRegistry.AZALEA_SHELF);

    public static final Item AZALEA_SIGN = register(
            BlockRegistry.AZALEA_SIGN, (block, settings) -> new SignItem(block, BlockRegistry.AZALEA_WALL_SIGN, settings), new Item.Properties().stacksTo(16)
    );
    public static final Item AZALEA_HANGING_SIGN = register(
            BlockRegistry.AZALEA_HANGING_SIGN,
            (block, settings) -> new HangingSignItem(block, BlockRegistry.AZALEA_WALL_HANGING_SIGN, settings),
            new Item.Properties().stacksTo(16)
    );

    public static final EntityType<Boat> AZALEA_BOAT_ENTITY = register2(
            "azalea_boat",
            EntityType.Builder.of(getBoatFactory(() -> ItemRegistry.AZALEA_BOAT), MobCategory.MISC)
                    .noLootTable()
                    .sized(1.375F, 0.5625F)
                    .eyeHeight(0.5625F)
                    .clientTrackingRange(10)
    );
    public static final EntityType<ChestBoat> AZALEA_CHEST_BOAT_ENTITY = register2(
            "azalea_chest_boat",
            EntityType.Builder.of(getChestBoatFactory(() -> ItemRegistry.AZALEA_CHEST_BOAT), MobCategory.MISC)
                    .noLootTable()
                    .sized(1.375F, 0.5625F)
                    .eyeHeight(0.5625F)
                    .clientTrackingRange(10)
    );

    public static final Item AZALEA_BOAT = register(
            "azalea_boat", settings -> new BoatItem(AZALEA_BOAT_ENTITY, settings), new Item.Properties().stacksTo(1)
    );
    public static final Item AZALEA_CHEST_BOAT = register(
            "azalea_chest_boat", settings -> new BoatItem(AZALEA_CHEST_BOAT_ENTITY, settings), new Item.Properties().stacksTo(1)
    );

    private static EntityType.EntityFactory<Boat> getBoatFactory(Supplier<Item> itemSupplier) {
        return (type, world) -> new Boat(type, world, itemSupplier);
    }
    private static EntityType.EntityFactory<ChestBoat> getChestBoatFactory(Supplier<Item> itemSupplier) {
        return (type, world) -> new ChestBoat(type, world, itemSupplier);
    }
    private static <T extends Entity> EntityType<T> register2(String id, EntityType.Builder<T> type) {
        return register2(keyOf2(id), type);
    }
    private static ResourceKey<EntityType<?>> keyOf2(String id) {
        return ResourceKey.create(Registries.ENTITY_TYPE, FixedMinecraft.id(id));
    }
    private static <T extends Entity> EntityType<T> register2(ResourceKey<EntityType<?>> key, EntityType.Builder<T> type) {
        return Registry.register(BuiltInRegistries.ENTITY_TYPE, key, type.build(key));
    }
}
