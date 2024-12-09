package net.greenjab.fixedminecraft.mixin.transport;

import net.greenjab.fixedminecraft.StatusEffects.StatusRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.potion.Potion;
import net.minecraft.potion.Potions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(Potions.class)
public abstract class PotionsMixin {

    @Redirect(method = "<clinit>",slice = @Slice(from = @At(value = "CONSTANT", args= {
            "stringValue=awkward"}, ordinal = 0)),at = @At(
            value = "NEW",target = "([Lnet/minecraft/entity/effect/StatusEffectInstance;)Lnet/minecraft/potion/Potion;", ordinal = 0 ))
    private static Potion useableTotem(StatusEffectInstance[] effects) {
        return new Potion(new StatusEffectInstance(StatusRegistry.INSTANCE.getAWKWARD(), 0));
    }
}
