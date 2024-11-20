package net.greenjab.fixedminecraft.mixin.enchanting;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.TridentEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public class EntityMixin {

    @Inject(method = "attemptTickInVoid", at = @At(
            value = "HEAD"
    ), cancellable = true)
    private void tridentReturnsFromVoid(CallbackInfo ci) {
        Entity E = (Entity) (Object)this;
        if (E instanceof TridentEntity TE) {
            if (TE.getY() < (double)(TE.getWorld().getBottomY() - 48)) {
                int i = EnchantmentHelper.getLevel(Enchantments.LOYALTY,TE.getItemStack());
                if (i>0) {
                    TE.addCommandTag("void");
                    TE.setVelocity(0, 0, 0);
                    ci.cancel();
                }
            }
        }
    }

    @Inject(method = "isBeingRainedOn", at = @At(value = "HEAD"),cancellable = true)
    private void wetLingeringEffect(CallbackInfoReturnable<Boolean> cir) {
        Entity E = (Entity) (Object) this;
        if (E instanceof PlayerEntity PE) {
            for (AreaEffectCloudEntity effectCloud : PE.getWorld().getNonSpectatingEntities(AreaEffectCloudEntity.class, PE.getBoundingBox())) {
                if (effectCloud.getColor() == 3694022) {
                    cir.setReturnValue(true);
                }
            }
        }
    }
}
