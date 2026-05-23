package net.greenjab.fixedminecraft.mixin.enchanting;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.gamerules.GameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public abstract class PlayerMixin {

    @Inject(method = "getBaseExperienceReward", at = @At("HEAD"), cancellable = true)
    private void halfLevelsOnDeath(CallbackInfoReturnable<Integer> cir, @Local(argsOnly = true) ServerLevel level) {
        Player player = (Player) (Object)this;
        if (!level.getGameRules().get(GameRules.KEEP_INVENTORY) && !player.isSpectator()) {
            int i = 0;
            for (int eLevel = 0; eLevel < player.experienceLevel / 2; eLevel++) {
                i +=getNextLevelExperience(eLevel);
            }
            if (player.experienceLevel%2==1) i +=getNextLevelExperience(player.experienceLevel/2)/2;
            i+= (int) (player.experienceProgress/2);
            cir.setReturnValue(i);
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
            target = "Lnet/minecraft/world/entity/player/Player;getEnchantedDamage(Lnet/minecraft/world/entity/Entity;FLnet/minecraft/world/damagesource/DamageSource;)F",
            ordinal = 0
    ))
    private float impalingEffectsWetMobs(float original, @Local(argsOnly = true) Entity entity) {
        if (entity instanceof LivingEntity) {
            Player PE = (Player) (Object) this;
            ItemEnchantments enchantments = PE.getMainHandItem().getEnchantments();
            int i = 0;
            for (Holder<Enchantment> entry : enchantments.keySet()) {
                if (entry.unwrapKey().isPresent() && entry.unwrapKey().get().equals(Enchantments.IMPALING)) {
                    i = enchantments.getLevel(entry);
                }
            }

            return original + ((entity.is(EntityTypeTags.AQUATIC) || entity.isInWaterOrRain()) ? i * 1.5F : 0.0F);
        }
        return original;
    }

    @ModifyExpressionValue(method = "getLuck", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;getAttributeValue(Lnet/minecraft/core/Holder;)D"))
    private double useLuckEffect(double original){
        Player PE = (Player) (Object)this;
        if (PE.hasEffect(MobEffects.LUCK)) {
            return (PE.getEffect(MobEffects.LUCK).getAmplifier()+1);
        }
        return 0;
    }
}
