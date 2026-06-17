package net.greenjab.fixedminecraft.mixin.redstone;

import net.greenjab.fixedminecraft.util.AmethystSculkSensorAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.SculkSensorBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SculkSensorBlockEntity.VibrationUser.class)
public abstract class SculkSensorBlockEntityMixin {

    @Shadow
    @Final
    protected BlockPos blockPos;

    @Inject(method = "canReceiveVibration", at = @At( value = "HEAD"), cancellable = true)
    private void setAmethystBlockAlreadyResonated(ServerLevel level, BlockPos pos, Holder<GameEvent> event,
                                                  GameEvent.@Nullable Context context, CallbackInfoReturnable<Boolean> cir) {
        BlockState sculkState = level.getBlockState(this.blockPos);
        if (sculkState.is(Blocks.CALIBRATED_SCULK_SENSOR)) {
            if (sculkState.getValue(AmethystSculkSensorAccessor.AMETHYST)) {
                if (context != null && context.affectedState() != null){
                    if (context.affectedState().is(Blocks.AMETHYST_BLOCK)) {
                        return;
                    }
                }
                cir.setReturnValue(false);
            }
        }
    }
}
