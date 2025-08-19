package net.greenjab.fixedminecraft.registry.block;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.DaylightDetectorBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class NewDaylightDetectorBlock extends DaylightDetectorBlock {

    public NewDaylightDetectorBlock(AbstractBlock.Settings settings) {
        super(settings);
    }

    @Override
    public boolean hasComparatorOutput(BlockState state)  {
        return true;
    }

    @Override
    public int getComparatorOutput(BlockState state, World world, BlockPos pos, Direction direction) {
        if (!world.isClient() && world.getDimension().hasSkyLight()) {
            boolean bl = state.get(INVERTED);
             if (bl) {
                 return world.getMoonPhase()+1;
            } else {
                 return (int)(((world.getTimeOfDay()+5000)%12000)/1000)+1;
            }
        }
        return 0;
    }
}
