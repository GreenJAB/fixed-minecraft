package net.greenjab.fixedminecraft.mixin.other;

import net.greenjab.fixedminecraft.registry.registries.GameRuleRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.attribute.EnvironmentAttributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HalfTransparentBlock;
import net.minecraft.world.level.block.IceBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.NonNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(IceBlock.class)
public abstract class IceBlockMixin extends HalfTransparentBlock {

    @Shadow
    protected abstract void melt(BlockState state, Level level, BlockPos pos);

    public IceBlockMixin(Properties properties) {super(properties);}

    @Override
    protected void randomTick(@NonNull BlockState state, ServerLevel level, @NonNull BlockPos pos, @NonNull RandomSource random) {
        if (level.getGameRules().get(GameRuleRegistry.ICE_MELT_IN_NETHER)) {
            if (level.getBrightness(LightLayer.BLOCK, pos) > 11 - state.getLightDampening() || level.environmentAttributes().getValue(EnvironmentAttributes.WATER_EVAPORATES, pos))
                if (notNextToCryingObsidian(level, pos)) this.melt(state, level, pos);
        }
    }

    @Unique
    private static boolean notNextToCryingObsidian(ServerLevel level, BlockPos pos) {
        for (Direction direction : Direction.values()) {
            if (level.getBlockState(pos.offset(direction.getUnitVec3i())).is(Blocks.CRYING_OBSIDIAN)) return false;
        }
        return true;
    }

}
