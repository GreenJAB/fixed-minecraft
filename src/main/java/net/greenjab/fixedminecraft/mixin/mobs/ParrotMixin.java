package net.greenjab.fixedminecraft.mixin.mobs;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.animal.parrot.Parrot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Parrot.class)
public abstract class ParrotMixin {

   @Inject(method = "mobInteract", at = @At(value = "HEAD"), cancellable = true)
    private void catchParrot(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir){
       ItemStack itemStack = player.getItemInHand(hand);
       Parrot PE = (Parrot)(Object)this;
       if (itemStack.isEmpty()) {
           if (PE.isFlying() && PE.isTame() && PE.isOwnedBy(player)) {
               if (!player.level().isClientSide()) {
                   if (PE.setEntityOnShoulder((ServerPlayer) player)) {
                       cir.setReturnValue(InteractionResult.SUCCESS);
                       cir.cancel();
                   }
               } else {
                   cir.setReturnValue(InteractionResult.SUCCESS);
                   cir.cancel();
               }
           }
       }
   }
}
