package net.greenjab.fixedminecraft.mixin.mobs;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.AbstractSkeletonEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Iterator;

@Mixin(AbstractSkeletonEntity.class)
public abstract class AbstractSkeletonEntityMixin  {
    @Inject(method = "createArrowProjectile", at = @At("RETURN"))
    private void tippedArrowFromEffect(ItemStack arrow, float damageModifier, ItemStack shotFrom,
                                       CallbackInfoReturnable<PersistentProjectileEntity> cir) {
        AbstractSkeletonEntity skele = (AbstractSkeletonEntity)(Object)this;
        if (!skele.getStatusEffects().isEmpty()) {
            if (skele.getEntityWorld().getDifficulty().getId() > 1) {
                if (Math.random() < (skele.getEntityWorld().getDifficulty().getId() - 1) / 10.0) {
                    PersistentProjectileEntity arrowProj = cir.getReturnValue();
                    if (arrowProj instanceof ArrowEntity arrowEntity) {
                        int effectCount = skele.getStatusEffects().size();
                        Iterator<StatusEffectInstance> iter = skele.getStatusEffects().iterator();
                        StatusEffectInstance effect = new StatusEffectInstance(StatusEffects.ABSORPTION, 200);
                        for (int i = 0; i<effectCount && iter.hasNext();i++ ) {
                            effect = iter.next();
                        }
                        arrowEntity.addEffect(new StatusEffectInstance(effect.getEffectType(), 200));
                    }
                }
            }
        }
    }
}
