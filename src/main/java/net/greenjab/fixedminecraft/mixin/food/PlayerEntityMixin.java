package net.greenjab.fixedminecraft.mixin.food;

import net.greenjab.fixedminecraft.network.SyncHandler;
import net.greenjab.fixedminecraft.registry.GameruleRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ConnectedClientData;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.Difficulty;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin
{
    @ModifyVariable(method = "addExhaustion", at = @At(value = "HEAD"), argsOnly = true)
    private float exhastionGamerule(float value) {
        PlayerEntity PE = (PlayerEntity)(Object)this;
        return value*PE.getWorld().getGameRules().getInt(GameruleRegistry.INSTANCE.getStamina_Drain_Speed()) / 100f;
    }
}
