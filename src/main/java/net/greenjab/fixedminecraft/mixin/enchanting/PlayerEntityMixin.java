package net.greenjab.fixedminecraft.mixin.enchanting;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.GameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {

    @Inject(method = "getXpToDrop", at = @At("HEAD"), cancellable = true)
    private void removeExclusivity(CallbackInfoReturnable<Integer> cir) {
        PlayerEntity player = (PlayerEntity) (Object)this;
        if (!player.getWorld().getGameRules().getBoolean(GameRules.KEEP_INVENTORY) && !player.isSpectator()) {
            int i = 0;
            for (int level = 0; level < player.experienceLevel/2;level++) {
                i +=getNextLevelExperience(level);
            }
            if (player.experienceLevel%2==1) i +=getNextLevelExperience(player.experienceLevel/2)/2;
            i+=player.experienceProgress/2;
            cir.setReturnValue(i+1);
        } else {
            cir.setReturnValue(0);
        }
    }

    public int getNextLevelExperience(int currentLevel) {
        if (currentLevel >= 30) {
            return 112 + (currentLevel - 30) * 9;
        } else {
            return currentLevel >= 15 ? 37 + (currentLevel - 15) * 5 : 7 + currentLevel * 2;
        }
    }
}
