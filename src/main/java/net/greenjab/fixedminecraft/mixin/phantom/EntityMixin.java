package net.greenjab.fixedminecraft.mixin.phantom;

import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.PhantomEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class EntityMixin {

    @Inject(method = "pushAwayFrom", at = @At(value = "HEAD"), cancellable = true)
    private void phantomsDontPush(Entity otherEntity, CallbackInfo ci){
        Entity thisEntity = (Entity)(Object)this;
        if (thisEntity instanceof PhantomEntity || otherEntity instanceof PhantomEntity) {
            ci.cancel();
        }
    }
}
