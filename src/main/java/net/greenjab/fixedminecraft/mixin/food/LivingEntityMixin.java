package net.greenjab.fixedminecraft.mixin.food;


import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShieldItem;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin
{
    @Shadow
    public abstract boolean isUsingItem();

    @Shadow
    public abstract ItemStack getStackInHand(Hand hand);

    @Shadow
    public abstract Hand getActiveHand();

    @Shadow
    public abstract void stopUsingItem();

    @Inject(method = "damage", at = @At(value = "INVOKE",
                                        target = "Lnet/minecraft/entity/damage/DamageSource;isIn(Lnet/minecraft/registry/tag/TagKey;)Z", ordinal = 5
    ))
    private void eatCancelling(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (this.isUsingItem()) {
            if (this.getStackInHand(this.getActiveHand()).isFood()) {
                LivingEntity LE = (LivingEntity)(Object)this;
                if (LE.getWorld().getDifficulty().getId()>1) {
                    this.stopUsingItem();
                }
            }
        }
    }
}
