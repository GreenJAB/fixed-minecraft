package net.greenjab.fixedminecraft.mixin.client;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Avatar;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AvatarRenderer.class)
public abstract class AvatarRendererMixin {

    @Inject(method = "getArmPose(Lnet/minecraft/world/entity/Avatar;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/client/model/HumanoidModel$ArmPose;", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/item/ItemStack;getUseAnimation()Lnet/minecraft/world/item/ItemUseAnimation;"
    ), cancellable = true)
    private static void animatedEating(Avatar avatar, ItemStack itemInHand, InteractionHand hand, CallbackInfoReturnable<HumanoidModel.ArmPose> cir){
        ItemUseAnimation useAction = itemInHand.getUseAnimation();
        if (useAction == ItemUseAnimation.EAT || useAction == ItemUseAnimation.DRINK) {
            cir.setReturnValue(HumanoidModel.ArmPose.TOOT_HORN);
        }
    }
}
