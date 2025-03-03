package net.greenjab.fixedminecraft.mixin.phantom;

import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.PhantomEntity;
import net.minecraft.entity.vehicle.FurnaceMinecartEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class EntityMixin {

    @Inject(method = "pushAwayFrom", at = @At(value = "HEAD"), cancellable = true)
    private void phantomsDontPush(Entity entity, CallbackInfo ci){
        if ((Entity)(Object)this instanceof PhantomEntity || entity instanceof PhantomEntity) {
            ci.cancel();
        }
    }

    @Redirect(method = "pushAwayFrom", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;addVelocity(DDD)V"))
    private void furnaceMinecartsCantBePushed(Entity instance, double deltaX, double deltaY, double deltaZ){
        if (!(instance instanceof FurnaceMinecartEntity)) {
            instance.addVelocity(deltaX, deltaY, deltaZ);
        }
    }
}
