package net.greenjab.fixedminecraft.mixin.horse;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.VehicleMoveC2SPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayNetworkHandler.class)
public class ServerPlayNetworkHandlerMixin {

    @Shadow
    public ServerPlayerEntity player;

    @WrapOperation(method = "onVehicleMove", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/entity/Entity;move(Lnet/minecraft/entity/MovementType;Lnet/minecraft/util/math/Vec3d;)V"
    ))
    private void tpHorseProperly(Entity instance, MovementType movementType, Vec3d movement, Operation<Void> original) {
        if (!instance.getCommandTags().contains("tp")) {
            instance.move(movementType, movement);
        }
    }

    @Inject(method = "onVehicleMove", at = @At("TAIL"))
    private void removeTag(VehicleMoveC2SPacket packet, CallbackInfo ci) {
        ServerPlayNetworkHandler SPNH = (ServerPlayNetworkHandler)(Object)this;
        Entity E = SPNH.player.getRootVehicle();
        E.removeCommandTag("tp");
    }

    @Redirect(method = "onPlayerAction", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/server/network/ServerPlayerEntity;getStackInHand(Lnet/minecraft/util/Hand;)Lnet/minecraft/item/ItemStack;"
    ))
    private ItemStack noNetheriteFix3(ServerPlayerEntity instance, Hand hand) {
        if (hand == Hand.MAIN_HAND) {
            return instance.equipment.get(EquipmentSlot.MAINHAND);
        }
        else if (hand == Hand.OFF_HAND) {
            return instance.equipment.get(EquipmentSlot.OFFHAND);
        }
        else {
            throw new IllegalArgumentException("Invalid hand " + hand);
        }
    }
}
