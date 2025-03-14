package net.greenjab.fixedminecraft.registry.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.block.Oxidizable;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;

public class OxidizableRailBlock extends CopperRailBlock implements Oxidizable {
    public static final MapCodec<OxidizableRailBlock> CODEC = RecordCodecBuilder.mapCodec(
             instance -> instance.group(
                            Oxidizable.OxidationLevel.CODEC.fieldOf("weathering_state").forGetter(OxidizableRailBlock::getDegradationLevel), createSettingsCodec()
                    )
                    .apply(instance, OxidizableRailBlock::new)
    );

    public OxidizableRailBlock(OxidationLevel oxidationLevel, Settings settings) {
        super(oxidationLevel, settings);
    }

    @Override
    public MapCodec<OxidizableRailBlock> getCodec(){ return CODEC;}

    @Override
    public boolean hasRandomTicks(BlockState state) {
        return Oxidizable.getIncreasedOxidationBlock(state.getBlock()).isPresent();
    }

    public Oxidizable.OxidationLevel getDegradationLevel() { return oxidationLevel; }

    @Override
    protected void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
    this.tickDegradation(state, world, pos, random);}

}
