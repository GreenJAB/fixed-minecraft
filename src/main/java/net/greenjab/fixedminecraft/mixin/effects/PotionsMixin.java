package net.greenjab.fixedminecraft.mixin.effects;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.greenjab.fixedminecraft.registry.registries.MobEffectRegistry;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.Potions;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(Potions.class)
public abstract class PotionsMixin {

    @WrapOperation(method="<clinit>", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/alchemy/Potions;register(Lnet/minecraft/resources/ResourceKey;Lnet/minecraft/world/item/alchemy/Potion;)Lnet/minecraft/core/Holder;", ordinal = 0), slice = @Slice( from = @At(
            value = "FIELD", target = "Lnet/minecraft/world/item/alchemy/Potions;THICK:Lnet/minecraft/core/Holder;", opcode = Opcodes.PUTSTATIC)))
    private static Holder<Potion> purpleAwkward(ResourceKey<Potion> key, Potion potion, Operation<Holder<Potion>> original) {
        return original.call(key, new Potion("awkward", new MobEffectInstance(MobEffectRegistry.AWKWARD, 0)));
    }
}
