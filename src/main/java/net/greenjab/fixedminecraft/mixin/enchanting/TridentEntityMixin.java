package net.greenjab.fixedminecraft.mixin.enchanting;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.passive.PigEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TridentEntity.class)
public class TridentEntityMixin {

    @Shadow
    @Final
    private static TrackedData<Boolean> ENCHANTED;

    @Inject(method = "<init>(Lnet/minecraft/world/World;Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/ItemStack;)V", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/entity/data/DataTracker;set(Lnet/minecraft/entity/data/TrackedData;Ljava/lang/Object;)V", ordinal = 0
    ))
    private void dispenserShoot(World world, LivingEntity owner, ItemStack stack, CallbackInfo ci) {
        if (owner instanceof PigEntity) {
            TridentEntity TE = (TridentEntity) (Object)this;
            TE.getDataTracker().set(ENCHANTED, stack.hasGlint());
            TE.pickupType = PersistentProjectileEntity.PickupPermission.ALLOWED;
        }
    }

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
