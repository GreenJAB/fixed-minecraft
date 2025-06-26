package net.greenjab.fixedminecraft.mixin.effects;

import net.greenjab.fixedminecraft.registry.registries.StatusRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.potion.Potion;
import net.minecraft.potion.Potions;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(Potions.class)
public abstract class PotionsMixin {

    @Redirect(method="<clinit>", at = @At( value = "INVOKE", target = "Lnet/minecraft/potion/Potions;register(Ljava/lang/String;Lnet/minecraft/potion/Potion;)Lnet/minecraft/registry/entry/RegistryEntry;", ordinal = 0 ), slice = @Slice( from = @At( value = "FIELD",
            target = "Lnet/minecraft/potion/Potions;THICK:Lnet/minecraft/registry/entry/RegistryEntry;")))
    private static RegistryEntry<Potion> purpleAwkward(String name, Potion potion) {
        return register(new Potion("awkward", new StatusEffectInstance(StatusRegistry.AWKWARD, 0)));
    }

    @Unique
    private static RegistryEntry<Potion> register(Potion potion) {
        return Registry.registerReference(Registries.POTION, Identifier.ofVanilla("awkward"), potion);
    }
}
