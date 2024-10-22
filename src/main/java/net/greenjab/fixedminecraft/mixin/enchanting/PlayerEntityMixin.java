package net.greenjab.fixedminecraft.mixin.enchanting;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.world.GameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {

    @Inject(method = "getXpToDrop", at = @At("HEAD"), cancellable = true)
    private void removeExclusivity(CallbackInfoReturnable<Integer> cir) {
        PlayerEntity player = (PlayerEntity) (Object)this;
        if (!player.getWorld().getGameRules().getBoolean(GameRules.KEEP_INVENTORY) && !player.isSpectator()) {
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
            target = "Lnet/minecraft/enchantment/EnchantmentHelper;getAttackDamage(Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/EntityGroup;)F",
            ordinal = 0
    ))
    private float impalingEffectsWetMobs(float original, @Local(argsOnly = true) Entity entity) {
        PlayerEntity PE = (PlayerEntity) (Object)this;
        int i = EnchantmentHelper.getLevel(Enchantments.IMPALING, PE.getMainHandStack());
        return original + ((((LivingEntity)entity).getGroup() == EntityGroup.AQUATIC || entity.isTouchingWaterOrRain()) ? i * 1.5F : 0.0F);
    }
}
