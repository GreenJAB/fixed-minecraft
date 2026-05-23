package net.greenjab.fixedminecraft.mixin.dragon;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.item.ItemEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin {

    @Inject(method = "hurtServer", at = @At(
            value = "HEAD"), cancellable = true)
    private void ignoreExplosions(ServerLevel level, DamageSource source, float damage, CallbackInfoReturnable<Boolean> cir) {
        if(source.getEntity() instanceof EnderDragon)cir.setReturnValue(false);
    }
}
