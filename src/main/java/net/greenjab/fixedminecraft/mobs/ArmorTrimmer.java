package net.greenjab.fixedminecraft.mobs;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.item.equipment.trim.ArmorTrim;
import net.minecraft.item.equipment.trim.ArmorTrimMaterial;
import net.minecraft.item.equipment.trim.ArmorTrimPattern;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class ArmorTrimmer {
    public static ItemStack trimAtChanceIfTrimable(ItemStack stack, @NotNull Random random, @NotNull DynamicRegistryManager registryManager) {
        return trimAtChanceIfTrimable(stack, random, registryManager, false);
    }

    public static ItemStack trimAtChanceIfTrimable(ItemStack stack, @NotNull Random random, @NotNull DynamicRegistryManager registryManager, boolean pale) {
        if (random.nextInt(10) > (pale?2:1)) {
            return stack;
        }

        Optional<Registry<ArmorTrimMaterial>> materialReference = registryManager.getOptional(RegistryKeys.TRIM_MATERIAL);
        Optional<Registry<ArmorTrimPattern>> patternReference = registryManager.getOptional(RegistryKeys.TRIM_PATTERN);


        if (materialReference.isEmpty() && patternReference.isEmpty()) {
            return stack;
        }

        ArmorTrim randomTrim = new ArmorTrim(materialReference.get().getRandom(random).get(), patternReference.get().getRandom(random).get());

        stack.set(DataComponentTypes.TRIM, randomTrim);


        return stack;
    }
}
