package net.greenjab.fixedminecraft.registry.block;

import net.greenjab.fixedminecraft.registry.registries.GameruleRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.IceBlock;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.LightType;
import net.minecraft.world.attribute.EnvironmentAttributes;

public class NewIceBlock extends IceBlock {

    public NewIceBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (world.getGameRules().getValue(GameruleRegistry.Ice_Melt_In_Nether)) {
            if (world.getLightLevel(LightType.BLOCK, pos) > 11 - state.getOpacity() || world.getEnvironmentAttributes().getAttributeValue(EnvironmentAttributes.WATER_EVAPORATES_GAMEPLAY, pos))
                if (!nextToCryingObsidian(world, pos)) this.melt(state, world, pos);
        }
    }

    public static boolean nextToCryingObsidian(ServerWorld world, BlockPos pos) {
        for (Direction direction : Direction.values()) {
            if (world.getBlockState(pos.add(direction.getVector())).isOf(Blocks.CRYING_OBSIDIAN)) return true;
        }
        return false;
    }
}
