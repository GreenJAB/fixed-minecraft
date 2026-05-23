package net.greenjab.fixedminecraft.mixin.redstone;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.attribute.EnvironmentAttributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.MoonPhase;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.DaylightDetectorBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.NonNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.minecraft.world.level.block.DaylightDetectorBlock.INVERTED;

@Mixin(DaylightDetectorBlock.class)
public abstract class DaylightDetectorBlockMixin extends BaseEntityBlock {
    protected DaylightDetectorBlockMixin(Properties properties) {
        super(properties);
    }

    @Inject(method = "updateSignalStrength", at = @At(value = "TAIL"))
    private static void updateComparators(BlockState state, Level level, BlockPos pos, CallbackInfo ci) {
        level.updateNeighbourForOutputSignal(pos, state.getBlock());
    }
    @Override
    public boolean hasAnalogOutputSignal(@NonNull BlockState state)  {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(@NonNull BlockState state, Level world, @NonNull BlockPos pos, @NonNull Direction direction) {
        if (!world.isClientSide() && world.dimensionType().hasSkyLight()) {
            boolean bl = state.getValue(INVERTED);
            if (bl) {
                MoonPhase moonPhase = (world).environmentAttributes().getValue(EnvironmentAttributes.MOON_PHASE, pos);
                return moonPhase.index()+1;
            } else {
                return (int)(((world.getOverworldClockTime()+5000)%12000)/1000)+1;
            }
        }
        return 0;
    }
}
