package net.greenjab.fixedminecraft.registry.block;

import net.greenjab.fixedminecraft.registry.registries.GameruleRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.block.IceBlock;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.LightType;

public class NewIceBlock extends IceBlock {

    public NewIceBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (world.getGameRules().getBoolean(GameruleRegistry.Ice_Melt_In_Nether)) {
            if (world.getLightLevel(LightType.BLOCK, pos) > 11 - state.getOpacity() || world.getDimension().ultrawarm())
                this.melt(state, world, pos);
        }
    }
}
