package net.greenjab.fixedminecraft.mixin.dragon;

import net.minecraft.block.entity.EndGatewayBlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EndGatewayBlockEntity.class)
public class EndGatewayBlockEntityMixin  {

    @Inject(method = "canTeleport", at = @At("HEAD"), cancellable = true)
    private static void dontTeleportDragon(Entity entity, CallbackInfoReturnable<Boolean> cir){
        if (entity instanceof EnderDragonEntity) {
            cir.setReturnValue(false);
            cir.cancel();
        }
    }
}
