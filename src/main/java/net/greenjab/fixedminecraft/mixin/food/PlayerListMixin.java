package net.greenjab.fixedminecraft.mixin.food;

import net.minecraft.network.Connection;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.Difficulty;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.greenjab.fixedminecraft.network.SyncHandler;

@Mixin(PlayerList.class)
public abstract class PlayerListMixin
{
    @Inject(at = @At("TAIL"), method = "placeNewPlayer")
    private void onPlayerConnect(Connection connection, ServerPlayer player, CommonListenerCookie cookie, CallbackInfo info)
    {
        SyncHandler.onPlayerLoggedIn(player);
    }
    @Redirect(method = "respawn", at = @At(value = "INVOKE",
                                                 target = "Lnet/minecraft/server/level/ServerPlayer;getHealth()F"
    ))
    private float dontSpawnAtMaxHealth(ServerPlayer instance){
        float multiplier = 1f;
        if (instance.level().getDifficulty() == Difficulty.NORMAL)multiplier=0.5f;
        if (instance.level().getDifficulty() == Difficulty.HARD)multiplier=0.3f;
        instance.getFoodData().setFoodLevel((int)(20*multiplier));
        instance.getFoodData().setSaturation((int)(20*multiplier));
        return instance.getHealth()*multiplier;
    }
}
