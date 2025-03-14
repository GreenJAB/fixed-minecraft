package net.greenjab.fixedminecraft.registry.registries;

import net.greenjab.fixedminecraft.registry.effect.CustomEffect;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

public class StatusRegistry {
    public static RegistryEntry<StatusEffect> AWKWARD =  registerStatusEffect("awkward", new CustomEffect(StatusEffectCategory.NEUTRAL,0xA72BEC));
    public static RegistryEntry<StatusEffect> REACH = registerStatusEffect("reach", new CustomEffect(StatusEffectCategory.NEUTRAL,0x98D982));
    public static RegistryEntry<StatusEffect> INSOMNIA = registerStatusEffect("insomnia", new CustomEffect(StatusEffectCategory.BENEFICIAL,0x98D982));

    private static RegistryEntry<StatusEffect> registerStatusEffect(String name, StatusEffect statusEffect) {
        return Registry.registerReference(Registries.STATUS_EFFECT, Identifier.of("fixedminecraft", name), statusEffect);
    }
}
