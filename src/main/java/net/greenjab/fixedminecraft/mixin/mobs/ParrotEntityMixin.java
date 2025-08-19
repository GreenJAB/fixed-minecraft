package net.greenjab.fixedminecraft.mixin.mobs;

import net.minecraft.entity.passive.ParrotEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ParrotEntity.class)
public abstract class ParrotEntityMixin {

   @Inject(method = "interactMob", at = @At(value = "HEAD"), cancellable = true)
    private void catchParrot(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir){
       ItemStack itemStack = player.getStackInHand(hand);
       ParrotEntity PE = (ParrotEntity)(Object)this;
       if (itemStack.isEmpty()) {
           if (PE.isInAir() && PE.isTamed() && PE.isOwner(player)) {
               if (!player.getEntityWorld().isClient()) {
                   if (PE.mountOnto((ServerPlayerEntity) player)) {
                       cir.setReturnValue(ActionResult.SUCCESS);
                       cir.cancel();
                   }
               } else {
                   cir.setReturnValue(ActionResult.SUCCESS);
                   cir.cancel();
               }
           }
       }
   }
}
