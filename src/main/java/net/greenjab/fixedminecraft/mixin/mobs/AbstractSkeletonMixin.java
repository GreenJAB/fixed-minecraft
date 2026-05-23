package net.greenjab.fixedminecraft.mixin.mobs;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Iterator;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.monster.skeleton.AbstractSkeleton;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.entity.projectile.arrow.Arrow;
import net.minecraft.world.item.ItemStack;

@Mixin(AbstractSkeleton.class)
public abstract class AbstractSkeletonMixin {
    @Inject(method = "getArrow", at = @At("RETURN"))
    private void tippedArrowFromEffect(ItemStack projectile, float power, ItemStack firingWeapon,
                                       CallbackInfoReturnable<AbstractArrow> cir) {
        AbstractSkeleton skele = (AbstractSkeleton)(Object)this;
        if (!skele.getActiveEffects().isEmpty()) {
            if (skele.level().getDifficulty().getId() > 1) {
                if (Math.random() < (skele.level().getDifficulty().getId() - 1) / 10.0) {
                    AbstractArrow arrowProj = cir.getReturnValue();
                    if (arrowProj instanceof Arrow arrowEntity) {
                        int effectCount = skele.getActiveEffects().size();
                        Iterator<MobEffectInstance> iter = skele.getActiveEffects().iterator();
                        MobEffectInstance effect = new MobEffectInstance(MobEffects.ABSORPTION, 200);
                        for (int i = 0; i<effectCount && iter.hasNext();i++ ) {
                            effect = iter.next();
                        }
                        arrowEntity.addEffect(new MobEffectInstance(effect.getEffect(), 200));
                    }
                }
            }
        }
    }
}
