package net.greenjab.fixedminecraft.mixin.enchanting;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.EntityTypeTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.GameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {

    @Inject(method = "getExperienceToDrop", at = @At("HEAD"), cancellable = true)
    private void removeExclusivity(CallbackInfoReturnable<Integer> cir, @Local(argsOnly = true) ServerWorld serverWorld) {
        PlayerEntity player = (PlayerEntity) (Object)this;
        if (!serverWorld.getGameRules().getBoolean(GameRules.KEEP_INVENTORY) && !player.isSpectator()) {
            int i = 0;
            for (int level = 0; level < player.experienceLevel/2;level++) {
                i +=getNextLevelExperience(level);
            }
            if (player.experienceLevel%2==1) i +=getNextLevelExperience(player.experienceLevel/2)/2;
            i+= (int) (player.experienceProgress/2);
            cir.setReturnValue(i+1);
        } else {
            cir.setReturnValue(0);
        }
    }

    @Unique
    public int getNextLevelExperience(int currentLevel) {
        if (currentLevel >= 30) {
            return 112 + (currentLevel - 30) * 9;
        } else {
            return currentLevel >= 15 ? 37 + (currentLevel - 15) * 5 : 7 + currentLevel * 2;
        }
    }
    @ModifyExpressionValue(method = "attack", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/entity/player/PlayerEntity;getDamageAgainst(Lnet/minecraft/entity/Entity;FLnet/minecraft/entity/damage/DamageSource;)F",
            ordinal = 0
    ))
    private float impalingEffectsWetMobs(float original, @Local(argsOnly = true) Entity entity) {
        PlayerEntity PE = (PlayerEntity) (Object)this;
        //int i = EnchantmentHelper.getLevel(Enchantments.IMPALING, PE.getMainHandStack());
        ItemEnchantmentsComponent enchantments = PE.getMainHandStack().getEnchantments();
        int i = 0;
        for (RegistryEntry<Enchantment> entry : enchantments.getEnchantments()) {
            if (entry.getKey().get().equals(Enchantments.IMPALING)) {
                i = enchantments.getLevel(entry);
            }
        }

        return original + ((((LivingEntity)entity).getType().isIn(EntityTypeTags.AQUATIC) || entity.isTouchingWaterOrRain()) ? i * 1.5F : 0.0F);
    }

    @ModifyExpressionValue(method = "getLuck", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;getAttributeValue(Lnet/minecraft/registry/entry/RegistryEntry;)D"))
    private double useLuckEffect(double original){
        PlayerEntity PE = (PlayerEntity) (Object)this;
        if (PE.hasStatusEffect(StatusEffects.LUCK)) {
            return (PE.getStatusEffect(StatusEffects.LUCK).getAmplifier()+1);
        }
        return 0;
    }
}
