package net.greenjab.fixedminecraft.mixin.food;

import net.greenjab.fixedminecraft.enchanting.FixedMinecraftEnchantmentHelper;
import net.greenjab.fixedminecraft.registry.registries.GameruleRegistry;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin
{
    @ModifyVariable(method = "addExhaustion", at = @At(value = "HEAD"), argsOnly = true)
    private float exhaustionGamerule(float value) {
        PlayerEntity PE = (PlayerEntity)(Object)this;
        if (PE.getEntityWorld() instanceof ServerWorld serverWorld) {
            return value* serverWorld.getGameRules().getValue(GameruleRegistry.Stamina_Drain_Speed)/100f;
        }
        return value;
    }

    @Redirect(method = "knockbackTarget", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;setSprinting(Z)V"))
    private void removeServerClientDesync(PlayerEntity instance, boolean b) {}

    @Inject(method = "canConsume", at = @At("HEAD"), cancellable = true)
    private void alwaysEatInPeaceful(boolean ignoreHunger, CallbackInfoReturnable<Boolean> cir) {
        PlayerEntity PE = (PlayerEntity)(Object)this;
        if (PE.getEntityWorld().getDifficulty().getId()==0) cir.setReturnValue(true);
    }

    @Redirect(method = "method_76458", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/HungerManager;canSprint()Z"))
    private boolean staminaCanLunge(HungerManager instance) {
        PlayerEntity PE = (PlayerEntity)(Object)this;
        if (PE.getEntityWorld() instanceof ServerWorld) {
            ItemStack weapon = PE.getWeaponStack();
            if (!weapon.isEmpty()) {
                int lungeLevel = FixedMinecraftEnchantmentHelper.enchantLevel(weapon, "lunge");
                if (lungeLevel > 0) {
                    float stamina = PE.getHungerManager().getSaturationLevel();
                    return !(stamina < lungeLevel * 2);
                }
            }
        }
        return true;
    }
}
