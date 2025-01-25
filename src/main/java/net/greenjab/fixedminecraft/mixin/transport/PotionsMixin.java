package net.greenjab.fixedminecraft.mixin.transport;

import net.greenjab.fixedminecraft.StatusEffects.StatusRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
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

import static net.minecraft.item.Items.register;

@Mixin(Potions.class)
public abstract class PotionsMixin {

    /*@Redirect(method = "<clinit>",slice = @Slice(from = @At(value = "CONSTANT", args= {
            "stringValue=awkward"}, ordinal = 0)),at = @At(
            value = "NEW",target = "(Lnet/minecraft/registry/entry/RegistryEntry;II)Lnet/minecraft/entity/effect/StatusEffectInstance;" ))
    private static StatusEffectInstance purpleAwkward(RegistryEntry<StatusEffect> effect, int duration, int amplifier) {
        return new StatusEffectInstance(new StatusEffectInstance(StatusRegistry.INSTANCE.getAWKWARD(), 0));
    }*/

    @Redirect(method="<clinit>", at = @At( value = "INVOKE", target = "Lnet/minecraft/potion/Potions;register(Ljava/lang/String;Lnet/minecraft/potion/Potion;)Lnet/minecraft/registry/entry/RegistryEntry;", ordinal = 0 ), slice = @Slice( from = @At( value = "FIELD",
            target = "Lnet/minecraft/potion/Potions;THICK:Lnet/minecraft/registry/entry/RegistryEntry;")))
    private static RegistryEntry<Potion> purpleAwkward(String name, Potion potion) {
        return register("awkward", new Potion("awkward", new StatusEffectInstance(StatusRegistry.INSTANCE.getAWKWARD(), 0)));
    }

    @Unique
    private static RegistryEntry<Potion> register(String name, Potion potion) {
        return Registry.registerReference(Registries.POTION, Identifier.ofVanilla(name), potion);
    }
}
