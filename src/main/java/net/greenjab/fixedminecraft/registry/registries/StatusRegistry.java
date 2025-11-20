package net.greenjab.fixedminecraft.registry.registries;

import com.mojang.serialization.MapCodec;
import net.greenjab.fixedminecraft.FixedMinecraft;
import net.greenjab.fixedminecraft.registry.effect.CustomEffect;
import net.greenjab.fixedminecraft.registry.other.DispencerMinecartEntity;
import net.greenjab.fixedminecraft.registry.other.ExplorationCompassLootFunction;
import net.minecraft.block.MapColor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.item.Item;
import net.minecraft.item.map.MapDecorationType;
import net.minecraft.loot.function.ExplorationMapLootFunction;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.loot.function.LootFunctionType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

import java.util.Optional;

public class StatusRegistry {
    //status effects
    public static RegistryEntry<StatusEffect> AWKWARD =  registerStatusEffect("awkward", new CustomEffect(StatusEffectCategory.NEUTRAL,0xA72BEC));
    public static RegistryEntry<StatusEffect> REACH = registerStatusEffect("reach", new CustomEffect(StatusEffectCategory.NEUTRAL,0x98D982));
    public static RegistryEntry<StatusEffect> INSOMNIA = registerStatusEffect("insomnia", new CustomEffect(StatusEffectCategory.BENEFICIAL,0x98D982));

    private static RegistryEntry<StatusEffect> registerStatusEffect(String name, StatusEffect statusEffect) {
        return Registry.registerReference(Registries.STATUS_EFFECT, Identifier.of("fixedminecraft", name), statusEffect);
    }


    //map decoration type
    public static RegistryEntry<MapDecorationType> PILLAGER_OUTPOST = registerMapDecorationType("outpost", "outpost", true, MapColor.BROWN.color, false, true);
    public static RegistryEntry<MapDecorationType> RUINED_PORTAL = registerMapDecorationType("ruined_portal", "ruined_portal", true, MapColor.PURPLE.color, false, true);
    public static RegistryEntry<MapDecorationType> TRAIL_RUINS = registerMapDecorationType("trail_ruins", "trail_ruins", true, MapColor.LIGHT_GRAY.color, false, true);

    private static RegistryEntry<MapDecorationType> registerMapDecorationType(
            String id, String assetId, boolean showOnItemFrame, int mapColor, boolean trackCount, boolean explorationMapElement
    ) {
        RegistryKey<MapDecorationType> registryKey = RegistryKey.of(RegistryKeys.MAP_DECORATION_TYPE, Identifier.ofVanilla(id));
        MapDecorationType mapDecorationType = new MapDecorationType(Identifier.ofVanilla(assetId), showOnItemFrame, mapColor, explorationMapElement, trackCount);
        return Registry.registerReference(Registries.MAP_DECORATION_TYPE, registryKey, mapDecorationType);
    }


    //loot function
    public static LootFunctionType<ExplorationCompassLootFunction> EXPLORATION_COMPASS = registerLootFunction("exploration_compass", ExplorationCompassLootFunction.CODEC);

    private static <T extends LootFunction> LootFunctionType<T> registerLootFunction(String id, MapCodec<T> codec) {
        return Registry.register(Registries.LOOT_FUNCTION_TYPE, FixedMinecraft.id(id), new LootFunctionType<>(codec));
    }


    //memory module
    public static final MemoryModuleType<Item> LAST_ITEM_TYPE = registerMemory("last_item_type");
    private static <U> MemoryModuleType<U> registerMemory(String id) {
        return Registry.register(Registries.MEMORY_MODULE_TYPE, Identifier.ofVanilla(id), new MemoryModuleType<>(Optional.empty()));
    }

    //entitytype
    public static final EntityType<DispencerMinecartEntity> DISPENCER_MINECART_ENTITY_TYPE = registerEntityType(
            keyOf("dispenser_minecart"), EntityType.Builder.create(DispencerMinecartEntity::new, SpawnGroup.MISC).dropsNothing().dimensions(0.98F, 0.7F).passengerAttachments(0.1875F).maxTrackingRange(8));

    private static <T extends Entity> EntityType<T> registerEntityType(RegistryKey<EntityType<?>> key, EntityType.Builder<T> type) {
        return Registry.register(Registries.ENTITY_TYPE, key, type.build(key));
    }
    private static RegistryKey<EntityType<?>> keyOf(String id) {
        return RegistryKey.of(RegistryKeys.ENTITY_TYPE, FixedMinecraft.id(id));
    }
}
