package net.greenjab.fixedminecraft.registry.other;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public class ColouredWoolProcessor implements StructureProcessor {
    public static final MapCodec<ColouredWoolProcessor> MAP_CODEC = RecordCodecBuilder.mapCodec(
             i -> i.group(
                            Codec.floatRange(0.0F, 1.0F).fieldOf("integrity").forGetter(t -> t.integrity)
                    )
                    .apply(i, ColouredWoolProcessor::new)
    );

    private final float integrity;

    private ColouredWoolProcessor(final float integrity) {
        this.integrity = integrity;
    }

    @Override
    public StructureTemplate.@Nullable StructureBlockInfo processBlock(
            final @NonNull LevelReader level,
            final @NonNull BlockPos targetPosition,
            final @NonNull BlockPos referencePos,
            final @NonNull BlockPos templateRelativePos,
            final StructureTemplate.StructureBlockInfo processedBlockInfo,
            final @NonNull StructurePlaceSettings settings
    ) {
        if (processedBlockInfo.state().is(Blocks.WOOL_STAIRS.white())) {
            RandomSource random = settings.getRandom(referencePos);
            Block colour = Blocks.WOOL_STAIRS.asList().get(random.nextInt(Blocks.WOOL_STAIRS.asList().size()));
            BlockState blockState = colour.defaultBlockState().withPropertiesOf(processedBlockInfo.state());
            return new StructureTemplate.StructureBlockInfo(processedBlockInfo.pos(), blockState, null);
        }
        return processedBlockInfo;
    }

    @Override
    public MapCodec<ColouredWoolProcessor> codec() {
        return MAP_CODEC;
    }
}
