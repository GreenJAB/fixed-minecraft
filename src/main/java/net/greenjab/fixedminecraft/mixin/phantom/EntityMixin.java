package net.greenjab.fixedminecraft.mixin.phantom;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.Phantom;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class EntityMixin {

    @Inject(method = "push(Lnet/minecraft/world/entity/Entity;)V", at = @At(value = "HEAD"), cancellable = true)
    private void phantomsDontPush(Entity entity, CallbackInfo ci){
        Entity thisEntity = (Entity)(Object)this;
        if (thisEntity instanceof Phantom || entity instanceof Phantom) {
            ci.cancel();
        }
    }
}
