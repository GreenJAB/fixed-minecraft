package net.greenjab.fixedminecraft.registry.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.AbstractRailBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Oxidizable;
import net.minecraft.block.enums.RailShape;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;

import static net.minecraft.block.Oxidizable.OxidationLevel.*;


public class CopperRailBlock extends AbstractRailBlock {
    //public static final MapCodec<CopperRailBlock> CODEC = createCodec(CopperRailBlock::new);
    public static final EnumProperty<RailShape> SHAPE = Properties.STRAIGHT_RAIL_SHAPE;
    public static final MapCodec<CopperRailBlock> CODEC = RecordCodecBuilder.mapCodec(
             instance -> instance.group(
                            Oxidizable.OxidationLevel.CODEC.fieldOf("weathering_state").forGetter(CopperRailBlock::getDegradationLevel), createSettingsCodec()
                    )
                    .apply(instance, CopperRailBlock::new)
    );
    public final Oxidizable.OxidationLevel oxidationLevel;
    public CopperRailBlock(Oxidizable.OxidationLevel oxidationLevel, AbstractBlock.Settings settings) {
        super(true, settings);
        this.oxidationLevel = oxidationLevel;
        this.setDefaultState(
                this.stateManager.getDefaultState().with(SHAPE, RailShape.NORTH_SOUTH).with(WATERLOGGED, false)
        );
    }

    @Override
    protected MapCodec<? extends AbstractRailBlock> getCodec() {
        return CODEC;
    }


    @Override
    public Property<RailShape> getShapeProperty() {
        return SHAPE;
    }
    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(SHAPE, WATERLOGGED);
    }

    public Oxidizable.OxidationLevel getDegradationLevel() { return oxidationLevel; }

    private static int getOxidisation(BlockState state) {
        if (state.getBlock().toString().toLowerCase().contains("oxidized")) return 3;
        if (state.getBlock().toString().toLowerCase().contains("weathered")) return 2;
        if (state.getBlock().toString().toLowerCase().contains("exposed")) return 1;
        if (state.getBlock().toString().toLowerCase().contains("copper_rail")) return 0;
        return -1;
    }

    public static double getMaxVelocity(BlockState state) {

        /*return switch (getOxidisation(state)) {
            case 0 -> 40.0;
            case 1 -> 20.0;
            case 2 -> 10.0;
            case 3 -> 5.0;
            default -> 8.0;
        };*/

        Oxidizable.OxidationLevel level = ((CopperRailBlock)state.getBlock()).oxidationLevel;
        if (level == UNAFFECTED) return 20.0;
        if (level == EXPOSED) return 15.0;
        if (level == WEATHERED) return 10.0;
        if (level == OXIDIZED) return 5.0;
        return 8.0;
    }







    @Override
    protected BlockState rotate(BlockState state, BlockRotation rotation) {
        switch (rotation) {
            case CLOCKWISE_180:
                switch ((RailShape)state.get(SHAPE)) {
                    case ASCENDING_EAST:
                        return state.with(SHAPE, RailShape.ASCENDING_WEST);
                    case ASCENDING_WEST:
                        return state.with(SHAPE, RailShape.ASCENDING_EAST);
                    case ASCENDING_NORTH:
                        return state.with(SHAPE, RailShape.ASCENDING_SOUTH);
                    case ASCENDING_SOUTH:
                        return state.with(SHAPE, RailShape.ASCENDING_NORTH);
                    case SOUTH_EAST:
                        return state.with(SHAPE, RailShape.NORTH_WEST);
                    case SOUTH_WEST:
                        return state.with(SHAPE, RailShape.NORTH_EAST);
                    case NORTH_WEST:
                        return state.with(SHAPE, RailShape.SOUTH_EAST);
                    case NORTH_EAST:
                        return state.with(SHAPE, RailShape.SOUTH_WEST);
                }
            case COUNTERCLOCKWISE_90:
                switch ((RailShape)state.get(SHAPE)) {
                    case ASCENDING_EAST:
                        return state.with(SHAPE, RailShape.ASCENDING_NORTH);
                    case ASCENDING_WEST:
                        return state.with(SHAPE, RailShape.ASCENDING_SOUTH);
                    case ASCENDING_NORTH:
                        return state.with(SHAPE, RailShape.ASCENDING_WEST);
                    case ASCENDING_SOUTH:
                        return state.with(SHAPE, RailShape.ASCENDING_EAST);
                    case SOUTH_EAST:
                        return state.with(SHAPE, RailShape.NORTH_EAST);
                    case SOUTH_WEST:
                        return state.with(SHAPE, RailShape.SOUTH_EAST);
                    case NORTH_WEST:
                        return state.with(SHAPE, RailShape.SOUTH_WEST);
                    case NORTH_EAST:
                        return state.with(SHAPE, RailShape.NORTH_WEST);
                    case NORTH_SOUTH:
                        return state.with(SHAPE, RailShape.EAST_WEST);
                    case EAST_WEST:
                        return state.with(SHAPE, RailShape.NORTH_SOUTH);
                }
            case CLOCKWISE_90:
                switch ((RailShape)state.get(SHAPE)) {
                    case ASCENDING_EAST:
                        return state.with(SHAPE, RailShape.ASCENDING_SOUTH);
                    case ASCENDING_WEST:
                        return state.with(SHAPE, RailShape.ASCENDING_NORTH);
                    case ASCENDING_NORTH:
                        return state.with(SHAPE, RailShape.ASCENDING_EAST);
                    case ASCENDING_SOUTH:
                        return state.with(SHAPE, RailShape.ASCENDING_WEST);
                    case SOUTH_EAST:
                        return state.with(SHAPE, RailShape.SOUTH_WEST);
                    case SOUTH_WEST:
                        return state.with(SHAPE, RailShape.NORTH_WEST);
                    case NORTH_WEST:
                        return state.with(SHAPE, RailShape.NORTH_EAST);
                    case NORTH_EAST:
                        return state.with(SHAPE, RailShape.SOUTH_EAST);
                    case NORTH_SOUTH:
                        return state.with(SHAPE, RailShape.EAST_WEST);
                    case EAST_WEST:
                        return state.with(SHAPE, RailShape.NORTH_SOUTH);
                }
            default:
                return state;
        }
    }

    @Override
    protected BlockState mirror(BlockState state, BlockMirror mirror) {
        RailShape railShape = state.get(SHAPE);
        switch (mirror) {
            case LEFT_RIGHT:
                switch (railShape) {
                    case ASCENDING_NORTH:
                        return state.with(SHAPE, RailShape.ASCENDING_SOUTH);
                    case ASCENDING_SOUTH:
                        return state.with(SHAPE, RailShape.ASCENDING_NORTH);
                    case SOUTH_EAST:
                        return state.with(SHAPE, RailShape.NORTH_EAST);
                    case SOUTH_WEST:
                        return state.with(SHAPE, RailShape.NORTH_WEST);
                    case NORTH_WEST:
                        return state.with(SHAPE, RailShape.SOUTH_WEST);
                    case NORTH_EAST:
                        return state.with(SHAPE, RailShape.SOUTH_EAST);
                    default:
                        return super.mirror(state, mirror);
                }
            case FRONT_BACK:
                switch (railShape) {
                    case ASCENDING_EAST:
                        return state.with(SHAPE, RailShape.ASCENDING_WEST);
                    case ASCENDING_WEST:
                        return state.with(SHAPE, RailShape.ASCENDING_EAST);
                    case ASCENDING_NORTH:
                    case ASCENDING_SOUTH:
                    default:
                        break;
                    case SOUTH_EAST:
                        return state.with(SHAPE, RailShape.SOUTH_WEST);
                    case SOUTH_WEST:
                        return state.with(SHAPE, RailShape.SOUTH_EAST);
                    case NORTH_WEST:
                        return state.with(SHAPE, RailShape.NORTH_EAST);
                    case NORTH_EAST:
                        return state.with(SHAPE, RailShape.NORTH_WEST);
                }
        }

        return super.mirror(state, mirror);
    }


}
