package net.greenjab.fixedminecraft.registry.registries;

import net.greenjab.fixedminecraft.registry.effect.CustomEffect;
import net.minecraft.block.MapColor;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.item.map.MapDecorationType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

public class StatusRegistry {
    public static RegistryEntry<StatusEffect> AWKWARD =  registerStatusEffect("awkward", new CustomEffect(StatusEffectCategory.NEUTRAL,0xA72BEC));
    public static RegistryEntry<StatusEffect> REACH = registerStatusEffect("reach", new CustomEffect(StatusEffectCategory.NEUTRAL,0x98D982));
    public static RegistryEntry<StatusEffect> INSOMNIA = registerStatusEffect("insomnia", new CustomEffect(StatusEffectCategory.BENEFICIAL,0x98D982));

    private static RegistryEntry<StatusEffect> registerStatusEffect(String name, StatusEffect statusEffect) {
        return Registry.registerReference(Registries.STATUS_EFFECT, Identifier.of("fixedminecraft", name), statusEffect);
    }



    public static RegistryEntry<MapDecorationType> PILLAGER_OUTPOST = registerMapDecorationType("outpost", "outpost", true, MapColor.LIGHT_GRAY.color, false, true);

    private static RegistryEntry<MapDecorationType> registerMapDecorationType(
            String id, String assetId, boolean showOnItemFrame, int mapColor, boolean trackCount, boolean explorationMapElement
    ) {
        RegistryKey<MapDecorationType> registryKey = RegistryKey.of(RegistryKeys.MAP_DECORATION_TYPE, Identifier.ofVanilla(id));
        MapDecorationType mapDecorationType = new MapDecorationType(Identifier.ofVanilla(assetId), showOnItemFrame, mapColor, explorationMapElement, trackCount);
        return Registry.registerReference(Registries.MAP_DECORATION_TYPE, registryKey, mapDecorationType);
    }
}
