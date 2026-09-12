package net.greenjab.fixedminecraft.registry.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.NonNull;

public class OxidizableRailBlock extends CopperRailBlock implements WeatheringCopper {

    private final WeatheringCopper.WeatherState weatherState;

    public OxidizableRailBlock(WeatheringCopper.WeatherState weatherState, Properties settings) {
        super(weatherState, settings);
        this.weatherState = weatherState;
    }

    @Override
    public boolean isRandomlyTicking(BlockState state) {
        return WeatheringCopper.getNext(state.getBlock()).isPresent();
    }

    @Override
    protected void randomTick(@NonNull BlockState state, @NonNull ServerLevel world, @NonNull BlockPos pos, @NonNull RandomSource random) {
    this.changeOverTime(state, world, pos, random);}

    @Override
    public @NonNull WeatherState getAge() {
        return this.weatherState;
    }
}
