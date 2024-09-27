package net.greenjab.fixedminecraft.mixin.util;

import net.greenjab.fixedminecraft.FixedMinecraft;
import net.greenjab.fixedminecraft.enchanting.Networking;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin {
    @Inject(method = "loadWorld", at = @At("RETURN"))
    private void loadWorld(CallbackInfo ci) {
        synchronized (Networking.SERVER_LOCK) {
            FixedMinecraft.INSTANCE.setSERVER((MinecraftServer) (Object) this);
            Networking.SERVER_LOCK.notifyAll();
        }
    }
}
