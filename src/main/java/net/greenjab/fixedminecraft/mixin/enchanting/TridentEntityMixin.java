package net.greenjab.fixedminecraft.mixin.enchanting;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.TridentEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(TridentEntity.class)
public class TridentEntityMixin {

    @Shadow
    public int returnTimer;

    @ModifyExpressionValue(method = "onEntityHit", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/enchantment/EnchantmentHelper;getAttackDamage(Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/EntityGroup;)F"
    ))
    private float impalingEffectsWetMobs(float original, @Local Entity entity) {
        TridentEntity TE = (TridentEntity) (Object)this;
        int i = EnchantmentHelper.getLevel(Enchantments.IMPALING, TE.getItemStack());
        return original + ((((LivingEntity)entity).getGroup() == EntityGroup.AQUATIC || entity.isTouchingWaterOrRain()) ? i * 1.5F : 0.0F);
    }

    @ModifyExpressionValue(method = "tick", at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/entity/projectile/TridentEntity;inGroundTime:I"
    ))
    private int returnVoidTrident(int original) {
        TridentEntity TE = (TridentEntity) (Object)this;
        if (TE.getCommandTags().contains("void")) return 5;
        return original;
    }
}
