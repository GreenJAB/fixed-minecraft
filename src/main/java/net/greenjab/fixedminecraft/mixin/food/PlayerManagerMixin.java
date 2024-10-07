package net.greenjab.fixedminecraft.mixin.food;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ConnectedClientData;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.Difficulty;
import net.minecraft.world.border.WorldBorder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.greenjab.fixedminecraft.network.SyncHandler;

@Mixin(PlayerManager.class)
public class PlayerManagerMixin
{
    @Inject(at = @At("TAIL"), method = "onPlayerConnect")
    private void onPlayerConnect(ClientConnection conn, ServerPlayerEntity player, ConnectedClientData clientData, CallbackInfo info)
    {
        SyncHandler.INSTANCE.onPlayerLoggedIn(player);
    }
    @Redirect(method = "respawnPlayer", at = @At(value = "INVOKE",
                                                 target = "Lnet/minecraft/server/network/ServerPlayerEntity;getHealth()F"
    ))
    private float dontSpawnAtMaxHealth(ServerPlayerEntity instance){
        float multiplier = 1f;
        if (instance.getWorld().getDifficulty() == Difficulty.NORMAL)multiplier=0.5f;
        if (instance.getWorld().getDifficulty() == Difficulty.HARD)multiplier=0.3f;
        instance.getHungerManager().setFoodLevel((int)(20*multiplier));
        instance.getHungerManager().setSaturationLevel((int)(20*multiplier));
        return instance.getHealth()*multiplier;
    }

    @ModifyArg(method = "sendWorldInfo", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/network/packet/s2c/play/WorldBorderInitializeS2CPacket;<init>(Lnet/minecraft/world/border/WorldBorder;)V"
    ), index = 0)
    private WorldBorder injected(WorldBorder value, @Local ServerPlayerEntity SPE) {
        return SPE.server.getWorld(SPE.getWorld().getRegistryKey()).getWorldBorder();//.getWorldBorder();
    }
}
