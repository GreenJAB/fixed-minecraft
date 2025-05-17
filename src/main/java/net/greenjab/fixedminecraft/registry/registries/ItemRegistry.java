package net.greenjab.fixedminecraft.registry.registries;


import net.greenjab.fixedminecraft.FixedMinecraft;
import net.greenjab.fixedminecraft.registry.item.map_book.MapBookAdditionsComponent;
import net.greenjab.fixedminecraft.registry.item.map_book.MapBookItem;
import net.greenjab.fixedminecraft.registry.item.EchoFruitItem;
import net.greenjab.fixedminecraft.registry.item.TotemItem;
import net.greenjab.fixedminecraft.registry.other.BaitComponent;
import net.minecraft.block.Block;
import net.minecraft.component.ComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.FireworksComponent;
import net.minecraft.component.type.FoodComponents;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.AnimalArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ArmorMaterials;
import net.minecraft.item.BlockItem;
import net.minecraft.item.FireworkRocketItem;
import net.minecraft.item.Item;
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
import java.util.function.UnaryOperator;

public class ItemRegistry {

    public static final Item DRAGON_FIREWORK_ROCKET = register(
            "dragon_firework_rocket", new FireworkRocketItem( new Item.Settings().component(DataComponentTypes.FIREWORKS, new FireworksComponent(1, List.of())))
    );
    public static final Item MAP_BOOK = register("map_book", new MapBookItem( new Item.Settings().maxCount(16)));
    public static final ComponentType<MapBookAdditionsComponent> MAP_BOOK_ADDITIONS = registerComponent("map_book_additions", (builder) -> builder.codec(MapBookAdditionsComponent.CODEC).packetCodec(MapBookAdditionsComponent.PACKET_CODEC).cache());

    public static final Item CHAINMAIL_HORSE_ARMOR = register(
            "chainmail_horse_armor", new AnimalArmorItem(ArmorMaterials.CHAIN, AnimalArmorItem.Type.EQUESTRIAN, false, new Item.Settings().maxCount(1))
            );


    public static final Item NETHERITE_HORSE_ARMOR = register(
            "netherite_horse_armor", new AnimalArmorItem(ArmorMaterials.NETHERITE, AnimalArmorItem.Type.EQUESTRIAN, false, new Item.Settings().maxCount(1).fireproof().rarity(Rarity.RARE)));

    public static final Item BROKEN_TOTEM = register("broken_totem", new Item(new Item.Settings().maxCount(1).rarity(Rarity.UNCOMMON)));
    public static final Item ECHO_TOTEM = register(
            "echo_totem", new TotemItem(new Item.Settings().maxCount(1).rarity(Rarity.UNCOMMON)));
    public static final Item ECHO_FRUIT = register(
            "echo_fruit", new EchoFruitItem(new Item.Settings().maxCount(1).rarity(Rarity.UNCOMMON).food(FoodComponents.CHORUS_FRUIT)));

    public static final Item NETHERITE_ANVIL = register(new BlockItem(BlockRegistry.NETHERITE_ANVIL, new Item.Settings().fireproof()));
    public static final Item CHIPPED_NETHERITE_ANVIL = register(new BlockItem(BlockRegistry.CHIPPED_NETHERITE_ANVIL, new Item.Settings().fireproof()));
    public static final Item DAMAGED_NETHERITE_ANVIL = register(new BlockItem(BlockRegistry.DAMAGED_NETHERITE_ANVIL, new Item.Settings().fireproof()));

    public static final Item COPPER_RAIL = register(BlockRegistry.COPPER_RAIL);
    public static final Item EXPOSED_COPPER_RAIL = register(BlockRegistry.EXPOSED_COPPER_RAIL);
    public static final Item WEATHERED_COPPER_RAIL = register(BlockRegistry.WEATHERED_COPPER_RAIL);
    public static final Item OXIDIZED_COPPER_RAIL = register(BlockRegistry.OXIDIZED_COPPER_RAIL);

    public static final Item WAXED_COPPER_RAIL = register(BlockRegistry.WAXED_COPPER_RAIL);
    public static final Item WAXED_EXPOSED_COPPER_RAIL = register(BlockRegistry.WAXED_EXPOSED_COPPER_RAIL);
    public static final Item WAXED_WEATHERED_COPPER_RAIL = register(BlockRegistry.WAXED_WEATHERED_COPPER_RAIL);
    public static final Item WAXED_OXIDIZED_COPPER_RAIL = register(BlockRegistry.WAXED_OXIDIZED_COPPER_RAIL);



    /*public static final ConsumableComponent GLOW_BERRIES_EFFECT = food()
            .consumeEffect(new ApplyEffectsConsumeEffect(new StatusEffectInstance(StatusEffects.GLOWING, 200, 0), 1F))
            .build();
    public static ConsumableComponent.Builder food() {
        return ConsumableComponent.builder().consumeSeconds(1.6F).useAction(UseAction.EAT).sound(SoundEvents.ENTITY_GENERIC_EAT).consumeParticles(true);
    }*/

    /** This is used, IntelliJ just doesn't realise */
    public static final RegistryEntry<Potion> BLINDNESS = register("blindness", new Potion("blindness", new StatusEffectInstance(StatusEffects.BLINDNESS, 800)));
    public static final RegistryEntry<Potion> LEVITATION = register("levitation", new Potion("levitation", new StatusEffectInstance(StatusEffects.LEVITATION, 1200)));

    public static final ComponentType<BaitComponent> BAIT_POWER = registerComponent("bait_power", (builder) -> builder.codec(BaitComponent.CODEC).packetCodec(BaitComponent.PACKET_CODEC).cache());


    public static Item register(Block block) {
        return register(new BlockItem(block, new Item.Settings()));
    }

    public static Item register(Block block, UnaryOperator<Item.Settings> settingsOperator) {
        return register(new BlockItem(block, (Item.Settings)settingsOperator.apply(new Item.Settings())));
    }

    public static Item register(Block block, Block... blocks) {
        BlockItem blockItem = new BlockItem(block, new Item.Settings());

        for (Block block2 : blocks) {
            Item.BLOCK_ITEMS.put(block2, blockItem);
        }

        return register(blockItem);
    }

    public static Item register(BlockItem item) {
        return register(item.getBlock(), item);
    }

    public static Item register(Block block, Item item) {
        return register(Registries.BLOCK.getId(block), item);
    }

    public static Item register(String id, Item item) {
        return register(Identifier.ofVanilla(id), item);
    }

    public static Item register(Identifier id, Item item) {
        return register(RegistryKey.of(Registries.ITEM.getKey(), id), item);
    }

    public static Item register(RegistryKey<Item> key, Item item) {
        if (item instanceof BlockItem) {
            ((BlockItem)item).appendBlocks(Item.BLOCK_ITEMS, item);
        }

        return Registry.register(Registries.ITEM, key, item);
    }


    private static <T> ComponentType<T> registerComponent(String id, UnaryOperator<ComponentType.Builder<T>> builderOperator) {
        return Registry.register(Registries.DATA_COMPONENT_TYPE, id, builderOperator.apply(ComponentType.builder()).build());
    }

    private static RegistryEntry<Potion> register(String name, Potion potion) {
        return Registry.registerReference(Registries.POTION, FixedMinecraft.id(name), potion);
    }
}
