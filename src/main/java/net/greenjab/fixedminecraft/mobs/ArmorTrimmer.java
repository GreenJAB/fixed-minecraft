package net.greenjab.fixedminecraft.mobs;

import net.minecraft.item.ItemStack;
import net.minecraft.item.trim.ArmorTrim;
import net.minecraft.item.trim.ArmorTrimMaterial;
import net.minecraft.item.trim.ArmorTrimPattern;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class ArmorTrimmer {
    public static ItemStack trimAtChanceIfTrimable(ItemStack stack, @NotNull Random random, @NotNull DynamicRegistryManager registryManager) {
        if (random.nextInt(10) > 1 /* TODO: Make chance configurable */) {
            return stack;
        }

        Optional<RegistryEntry.Reference<ArmorTrimMaterial>> materialReference = registryManager.get(RegistryKeys.TRIM_MATERIAL).getRandom(random);
        Optional<RegistryEntry.Reference<ArmorTrimPattern>> patternReference = registryManager.get(RegistryKeys.TRIM_PATTERN).getRandom(random);

        if (materialReference.isEmpty() && patternReference.isEmpty()) {
            return stack;
        }

        ArmorTrim randomTrim = new ArmorTrim(materialReference.get(), patternReference.get());
        ArmorTrim.apply(registryManager, stack, randomTrim);

        return stack;
    }
}
