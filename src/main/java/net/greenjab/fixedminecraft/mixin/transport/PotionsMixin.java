package net.greenjab.fixedminecraft.mixin.transport;

import net.greenjab.fixedminecraft.StatusEffects.StatusRegistry;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.potion.Potion;
import net.minecraft.potion.Potions;
import net.minecraft.registry.entry.RegistryEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(Potions.class)
public abstract class PotionsMixin {

    @Redirect(method = "<clinit>",slice = @Slice(from = @At(value = "CONSTANT", args= {
            "stringValue=awkward"}, ordinal = 0)),at = @At(
            value = "NEW",target = "(Lnet/minecraft/registry/entry/RegistryEntry;II)Lnet/minecraft/entity/effect/StatusEffectInstance;" ))
    private static StatusEffectInstance purpleAwkward(RegistryEntry<StatusEffect> effect, int duration, int amplifier) {
        return new StatusEffectInstance(new StatusEffectInstance(StatusRegistry.INSTANCE.getAWKWARD(), 0));
    }
}
