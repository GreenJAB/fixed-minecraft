package net.greenjab.fixedminecraft.mixin.enchanting;

import net.greenjab.fixedminecraft.registry.other.NewAnvilMenu;
import net.minecraft.network.protocol.PacketUtils;
import net.minecraft.network.protocol.game.ServerboundRenameItemPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerGamePacketListenerImpl.class)
public abstract class ServerGamePacketListenerImplMixin {

    @Shadow
    public ServerPlayer player;

    @Shadow
    @Final
    private static Logger LOGGER;

    @Inject(method = "handleRenameItem", at = @At("HEAD"), cancellable = true)
    private void addGreenGlintUpdate(ServerboundRenameItemPacket packet, CallbackInfo ci) {
        PacketUtils.ensureRunningOnSameThread(packet, (ServerGamePacketListenerImpl)(Object)this, this.player.level());
        if (this.player.containerMenu instanceof NewAnvilMenu menu) {
            if (!menu.stillValid(this.player)) {
                LOGGER.debug("Player {} interacted with invalid menu {}", this.player, menu);
                ci.cancel();
                return;
            }
            menu.setItemName(packet.getName());
        }
    }
}
