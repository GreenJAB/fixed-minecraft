package net.greenjab.fixedminecraft.mixin.client;

import kotlin.jvm.JvmStatic;
import net.greenjab.fixedminecraft.StatusEffects.StatusRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ElytraItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.stat.Stats;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.dimension.DimensionTypes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.awt.*;


@SuppressWarnings("unchecked")
@Mixin(ClientPlayerEntity.class)
public class ClientPlayerEntityMixin {
    @Shadow
    @Final
    protected MinecraftClient client;

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
        if (CPE.input.jumping) {
            ItemStack itemStack = CPE.getEquippedStack(EquipmentSlot.CHEST);
            if (itemStack.isOf(Items.ELYTRA) && ElytraItem.isUsable(itemStack) && CPE.checkFallFlying()) {
                CPE.networkHandler.sendPacket(new ClientCommandC2SPacket(CPE, ClientCommandC2SPacket.Mode.START_FALL_FLYING));
            }
        }

        if ( this.client.world.getDimensionKey() == DimensionTypes.THE_END) {
            if (this.client.player.getStatHandler().getStat(Stats.KILLED.getOrCreateStat(EntityType.ENDER_DRAGON))==0) {
                this.client.world.getWorldBorder().setSize(400);
            } else {
                this.client.world.getWorldBorder().setSize(60000000);
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
