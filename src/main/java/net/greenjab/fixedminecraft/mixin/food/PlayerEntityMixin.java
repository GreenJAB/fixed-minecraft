package net.greenjab.fixedminecraft.mixin.food;

import net.greenjab.fixedminecraft.registry.GameruleRegistry;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.Objects;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin
{
    @ModifyVariable(method = "addExhaustion", at = @At(value = "HEAD"), argsOnly = true)
    private float exhastionGamerule(float value) {
        PlayerEntity PE = (PlayerEntity)(Object)this;
        return value* Objects.requireNonNull(Objects.requireNonNull(PE.getServer()).getWorld(PE.getWorld().getRegistryKey())).getGameRules().getInt(GameruleRegistry.INSTANCE.getStamina_Drain_Speed()) / 100f;
    }
}
