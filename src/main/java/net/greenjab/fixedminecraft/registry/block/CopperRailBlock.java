package net.greenjab.fixedminecraft.registry.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.RailShape;
import org.jspecify.annotations.NonNull;

import static net.minecraft.world.level.block.WeatheringCopper.WeatherState.*;


public class CopperRailBlock extends BaseRailBlock {
    public static final EnumProperty<RailShape> SHAPE = BlockStateProperties.RAIL_SHAPE_STRAIGHT;
    public static final MapCodec<CopperRailBlock> CODEC = RecordCodecBuilder.mapCodec(
             instance -> instance.group(
                            WeatheringCopper.WeatherState.CODEC.fieldOf("weathering_state").forGetter(CopperRailBlock::getDegradationLevel), propertiesCodec()
                    )
                    .apply(instance, CopperRailBlock::new)
    );
    public final WeatheringCopper.WeatherState oxidationLevel;
    public CopperRailBlock(WeatheringCopper.WeatherState oxidationLevel, BlockBehaviour.Properties settings) {
        super(true, settings);
        this.oxidationLevel = oxidationLevel;
        this.registerDefaultState(
                this.stateDefinition.any().setValue(SHAPE, RailShape.NORTH_SOUTH).setValue(WATERLOGGED, false)
        );
    }

    @Override
    protected @NonNull MapCodec<? extends BaseRailBlock> codec() {
        return CODEC;
    }


    @Override
    public @NonNull Property<RailShape> getShapeProperty() {
        return SHAPE;
    }
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(SHAPE, WATERLOGGED);
    }

    public WeatheringCopper.WeatherState getDegradationLevel() { return oxidationLevel; }

    public static double getMaxVelocity(BlockState state) {
        WeatheringCopper.WeatherState level = ((CopperRailBlock)state.getBlock()).oxidationLevel;
        if (level == UNAFFECTED) return 40.0;
        if (level == EXPOSED) return 20.0;
        if (level == WEATHERED) return 10.0;
        if (level == OXIDIZED) return 5.0;
        return 8.0;
    }

    @Override
    protected @NonNull BlockState rotate(@NonNull BlockState state, Rotation rotation) {
        return switch (rotation) {
            case CLOCKWISE_180 -> switch (state.getValue(SHAPE)) {
                case ASCENDING_EAST -> state.setValue(SHAPE, RailShape.ASCENDING_WEST);
                case ASCENDING_WEST -> state.setValue(SHAPE, RailShape.ASCENDING_EAST);
                case ASCENDING_NORTH -> state.setValue(SHAPE, RailShape.ASCENDING_SOUTH);
                case ASCENDING_SOUTH -> state.setValue(SHAPE, RailShape.ASCENDING_NORTH);
                case SOUTH_EAST -> state.setValue(SHAPE, RailShape.NORTH_WEST);
                case SOUTH_WEST -> state.setValue(SHAPE, RailShape.NORTH_EAST);
                case NORTH_WEST -> state.setValue(SHAPE, RailShape.SOUTH_EAST);
                case NORTH_EAST -> state.setValue(SHAPE, RailShape.SOUTH_WEST);
                case NORTH_SOUTH -> state.setValue(SHAPE, RailShape.NORTH_SOUTH);
                case EAST_WEST -> state.setValue(SHAPE, RailShape.EAST_WEST);
            };
            case COUNTERCLOCKWISE_90 -> switch (state.getValue(SHAPE)) {
                case ASCENDING_EAST -> state.setValue(SHAPE, RailShape.ASCENDING_NORTH);
                case ASCENDING_WEST -> state.setValue(SHAPE, RailShape.ASCENDING_SOUTH);
                case ASCENDING_NORTH -> state.setValue(SHAPE, RailShape.ASCENDING_WEST);
                case ASCENDING_SOUTH -> state.setValue(SHAPE, RailShape.ASCENDING_EAST);
                case SOUTH_EAST -> state.setValue(SHAPE, RailShape.NORTH_EAST);
                case SOUTH_WEST -> state.setValue(SHAPE, RailShape.SOUTH_EAST);
                case NORTH_WEST -> state.setValue(SHAPE, RailShape.SOUTH_WEST);
                case NORTH_EAST -> state.setValue(SHAPE, RailShape.NORTH_WEST);
                case NORTH_SOUTH -> state.setValue(SHAPE, RailShape.EAST_WEST);
                case EAST_WEST -> state.setValue(SHAPE, RailShape.NORTH_SOUTH);
            };
            case CLOCKWISE_90 -> switch (state.getValue(SHAPE)) {
                case ASCENDING_EAST -> state.setValue(SHAPE, RailShape.ASCENDING_SOUTH);
                case ASCENDING_WEST -> state.setValue(SHAPE, RailShape.ASCENDING_NORTH);
                case ASCENDING_NORTH -> state.setValue(SHAPE, RailShape.ASCENDING_EAST);
                case ASCENDING_SOUTH -> state.setValue(SHAPE, RailShape.ASCENDING_WEST);
                case SOUTH_EAST -> state.setValue(SHAPE, RailShape.SOUTH_WEST);
                case SOUTH_WEST -> state.setValue(SHAPE, RailShape.NORTH_WEST);
                case NORTH_WEST -> state.setValue(SHAPE, RailShape.NORTH_EAST);
                case NORTH_EAST -> state.setValue(SHAPE, RailShape.SOUTH_EAST);
                case NORTH_SOUTH -> state.setValue(SHAPE, RailShape.EAST_WEST);
                case EAST_WEST -> state.setValue(SHAPE, RailShape.NORTH_SOUTH);
            };
            default -> state;
        };
    }

    @Override
    protected @NonNull BlockState mirror(BlockState state, Mirror mirror) {
        RailShape railShape = state.getValue(SHAPE);
        switch (mirror) {
            case LEFT_RIGHT:
                return switch (railShape) {
                    case ASCENDING_NORTH -> state.setValue(SHAPE, RailShape.ASCENDING_SOUTH);
                    case ASCENDING_SOUTH -> state.setValue(SHAPE, RailShape.ASCENDING_NORTH);
                    case SOUTH_EAST -> state.setValue(SHAPE, RailShape.NORTH_EAST);
                    case SOUTH_WEST -> state.setValue(SHAPE, RailShape.NORTH_WEST);
                    case NORTH_WEST -> state.setValue(SHAPE, RailShape.SOUTH_WEST);
                    case NORTH_EAST -> state.setValue(SHAPE, RailShape.SOUTH_EAST);
                    default -> super.mirror(state, mirror);
                };
            case FRONT_BACK:
                switch (railShape) {
                    case ASCENDING_EAST:
                        return state.setValue(SHAPE, RailShape.ASCENDING_WEST);
                    case ASCENDING_WEST:
                        return state.setValue(SHAPE, RailShape.ASCENDING_EAST);
                    case SOUTH_EAST:
                        return state.setValue(SHAPE, RailShape.SOUTH_WEST);
                    case SOUTH_WEST:
                        return state.setValue(SHAPE, RailShape.SOUTH_EAST);
                    case NORTH_WEST:
                        return state.setValue(SHAPE, RailShape.NORTH_EAST);
                    case NORTH_EAST:
                        return state.setValue(SHAPE, RailShape.NORTH_WEST);
                }
        }

        return super.mirror(state, mirror);
    }


}
