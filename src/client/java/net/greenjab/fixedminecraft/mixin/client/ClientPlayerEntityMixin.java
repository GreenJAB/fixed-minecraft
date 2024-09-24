package net.greenjab.fixedminecraft.mixin.client;

import kotlin.jvm.JvmStatic;
import net.greenjab.fixedminecraft.StatusEffects.StatusRegistry;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ElytraItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@SuppressWarnings("unchecked")
@Mixin(ClientPlayerEntity.class)
public class ClientPlayerEntityMixin {
    @Inject(method = "canSprint", at = @At("HEAD"), cancellable = true)
    private void cancelSprintAt0Saturation(CallbackInfoReturnable cir) {
        PlayerEntity instance = (PlayerEntity)(Object)this;
        cir.setReturnValue(instance.hasVehicle() || (float)instance.getHungerManager().getSaturationLevel() > 0.0F || instance.getAbilities().allowFlying);
    }

    @Redirect(method = "tickMovement", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;isClimbing()Z"))
    private boolean failRealTest(ClientPlayerEntity instance) {
        return true;
    }
    @Inject(method = "tickMovement", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;isFallFlying()Z"))
    private void addMyTest(CallbackInfo ci) {
        ClientPlayerEntity CPE = (ClientPlayerEntity)(Object)this;
        if (CPE.input.jumping && !CPE.getAbilities().flying && !CPE.hasVehicle() && !CPE.isClimbing()) {
            ItemStack itemStack = CPE.getEquippedStack(EquipmentSlot.CHEST);
            if (itemStack.isOf(Items.ELYTRA) && ElytraItem.isUsable(itemStack) && CPE.checkFallFlying()) {
                CPE.networkHandler.sendPacket(new ClientCommandC2SPacket(CPE, ClientCommandC2SPacket.Mode.START_FALL_FLYING));
            }
        }
    }
}
