package net.greenjab.fixedminecraft.mixin.util;

import net.greenjab.fixedminecraft.FixedMinecraft;
import net.greenjab.fixedminecraft.enchanting.Networking;
import net.greenjab.fixedminecraft.registry.item.map_book.MapBookState;
import net.greenjab.fixedminecraft.registry.item.map_book.MapBookStateManager;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin {
    @Inject(method = "tick", at = @At("RETURN"))
    private void loadWorld(CallbackInfo ci) {
        MinecraftServer SW = (MinecraftServer)(Object) this;
        synchronized (Networking.SERVER_LOCK) {
            FixedMinecraft.INSTANCE.setSERVER(SW);
            Networking.SERVER_LOCK.notifyAll();
        }
        for (int id : MapBookStateManager.INSTANCE.currentBooks) {
            MapBookState state = MapBookStateManager.INSTANCE.getMapBookState(SW, id);
            if (state != null) {
                state.sendData(SW, id);
            }
        }
        MapBookStateManager.INSTANCE.currentBooks = (new ArrayList<>());
    }
}
