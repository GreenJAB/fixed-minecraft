package net.greenjab.fixedminecraft.mixin.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ElytraItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(ClientPlayerEntity.class)
public class ClientPlayerEntityMixin {
    @Shadow
    @Final
    protected MinecraftClient client;

    @Inject(method = "canSprint", at = @At("HEAD"), cancellable = true)
    private void cancelSprintAt0Saturation(CallbackInfoReturnable<Boolean> cir) {
        PlayerEntity instance = (PlayerEntity)(Object)this;
        cir.setReturnValue(instance.hasVehicle() || instance.getHungerManager().getSaturationLevel() > 0.0F || instance.getAbilities().allowFlying);
    }

    @Redirect(method = "tickMovement", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;isClimbing()Z"))
    private boolean failRealTest(ClientPlayerEntity instance) {
        return true;
    }

    @Inject(method = "tickMovement", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;isFallFlying()Z"))
    private void addMyTest(CallbackInfo ci) {
        ClientPlayerEntity CPE = (ClientPlayerEntity)(Object)this;
        if (CPE.input.jumping) {
            ItemStack itemStack = CPE.getEquippedStack(EquipmentSlot.CHEST);
            if (itemStack.isOf(Items.ELYTRA) && ElytraItem.isUsable(itemStack) && CPE.checkFallFlying()) {
                CPE.networkHandler.sendPacket(new ClientCommandC2SPacket(CPE, ClientCommandC2SPacket.Mode.START_FALL_FLYING));
            }
        }
    }
    @Inject(method = "canVehicleSprint", at = @At("HEAD"), cancellable = true)
    private void horsesCanSprint(Entity vehicle, CallbackInfoReturnable<Boolean> cir){
        if (vehicle instanceof AbstractHorseEntity) {
            cir.setReturnValue(true);
        }
    }
}
