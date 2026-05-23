package net.greenjab.fixedminecraft.mixin.horse;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.network.protocol.game.ServerboundMoveVehiclePacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerGamePacketListenerImpl.class)
public abstract class ServerGamePacketListenerImplMixin {

    @Shadow
    public ServerPlayer player;

    @WrapOperation(method = "handleMoveVehicle", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/Entity;move(Lnet/minecraft/world/entity/MoverType;Lnet/minecraft/world/phys/Vec3;)V"
    ))
    private void tpHorseProperly(Entity instance, MoverType moverType, Vec3 delta, Operation<Void> original) {
        if (!instance.entityTags().contains("tp")) {
            instance.move(moverType, delta);
        }
    }

    @Inject(method = "handleMoveVehicle", at = @At("TAIL"))
    private void removeTag(ServerboundMoveVehiclePacket packet, CallbackInfo ci) {
        ServerGamePacketListenerImpl SPNH = (ServerGamePacketListenerImpl)(Object)this;
        Entity E = SPNH.player.getRootVehicle();
        E.removeTag("tp");
    }
}
