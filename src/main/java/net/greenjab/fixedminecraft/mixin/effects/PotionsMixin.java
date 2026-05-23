package net.greenjab.fixedminecraft.mixin.effects;

import net.greenjab.fixedminecraft.registry.registries.MobEffectRegistry;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.Potions;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(Potions.class)
public abstract class PotionsMixin {

    @Redirect(method="<clinit>", at = @At( value = "INVOKE", target = "Lnet/minecraft/world/item/alchemy/Potions;register(Ljava/lang/String;Lnet/minecraft/world/item/alchemy/Potion;)Lnet/minecraft/core/Holder$Reference;",ordinal = 0), slice = @Slice( from = @At(
            value = "FIELD",
            target = "Lnet/minecraft/world/item/alchemy/Potions;THICK:Lnet/minecraft/core/Holder$Reference;",
            opcode = Opcodes.PUTSTATIC
    )))
    private static Holder.Reference<Potion> purpleAwkward(String name, Potion potion) {
        return register(new Potion("awkward", new MobEffectInstance(MobEffectRegistry.AWKWARD, 0)));
    }

    @Unique
    private static Holder.Reference<Potion> register(Potion potion) {
        return Registry.registerForHolder(BuiltInRegistries.POTION, Identifier.withDefaultNamespace("awkward"), potion);
    }
}
