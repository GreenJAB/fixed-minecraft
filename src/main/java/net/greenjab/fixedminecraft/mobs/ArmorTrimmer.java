package net.greenjab.fixedminecraft.mobs;

import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.trim.ArmorTrim;
import net.minecraft.world.item.equipment.trim.TrimMaterial;
import net.minecraft.world.item.equipment.trim.TrimPattern;
/** Credit: Laazuli */
public class ArmorTrimmer {
    public static ItemStack trimAtChanceIfTrimable(ItemStack stack, @NotNull RandomSource random, @NotNull RegistryAccess registryManager) {
        return trimAtChanceIfTrimable(stack, random, registryManager, false);
    }

    public static ItemStack trimAtChanceIfTrimable(ItemStack stack, @NotNull RandomSource random, @NotNull RegistryAccess registryManager, boolean pale) {
        if (random.nextInt(10) > (pale?2:1)) {
            return stack;
        }

        Optional<Registry<TrimMaterial>> materialReference = registryManager.lookup(Registries.TRIM_MATERIAL);
        Optional<Registry<TrimPattern>> patternReference = registryManager.lookup(Registries.TRIM_PATTERN);


        if (materialReference.isEmpty() && patternReference.isEmpty()) {
            return stack;
        }

        ArmorTrim randomTrim = new ArmorTrim(materialReference.get().getRandom(random).get(), patternReference.get().getRandom(random).get());

        stack.set(DataComponents.TRIM, randomTrim);


        return stack;
    }
}
