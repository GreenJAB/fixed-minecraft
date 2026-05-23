package net.greenjab.fixedminecraft.mixin.structure;

import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.Aquifer;
import net.minecraft.world.level.levelgen.NoiseChunk;
import net.minecraft.world.level.levelgen.NoiseRouter;
import net.minecraft.world.level.levelgen.PositionalRandomFactory;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Arrays;

//Credits: Amrsatrio - SinkHoleRestorer
@Mixin(Aquifer.NoiseBasedAquifer.class)
public abstract class NoiseBasedAquifierMixin {

    @Unique
    private static final int[][] SURFACE_SAMPLING_OFFSETS_IN_CHUNKS_1_19_3 = new int[][]{
            {0, 0}, {-2, -1}, {-1, -1}, {0, -1}, {1, -1}, {-3, 0}, {-2, 0}, {-1, 0}, {1, 0}, {-2, 1}, {-1, 1}, {0, 1}, {1, 1}
    };

    @Unique
    private static final int[][] SURFACE_SAMPLING_OFFSETS_IN_CHUNKS_PRE_1_19_3 = new int[][]{
            {-2, -1}, {-1, -1}, {0, -1}, {1, -1}, {-3, 0}, {-2, 0}, {-1, 0}, {0, 0}, {1, 0}, {-2, 1}, {-1, 1}, {0, 1}, {1, 1}
    };

    @Mutable
    @Shadow
    @Final
    private static int[][] SURFACE_SAMPLING_OFFSETS_IN_CHUNKS;

    @Mutable
    @Shadow
    @Final
    private int skipSamplingAboveY;

    @Inject(method = "<clinit>", at = @At(value = "TAIL"))
    private static void restoreOldSamplingOffsets(CallbackInfo ci) {
        if (Arrays.deepEquals(SURFACE_SAMPLING_OFFSETS_IN_CHUNKS, SURFACE_SAMPLING_OFFSETS_IN_CHUNKS_1_19_3)) {
            SURFACE_SAMPLING_OFFSETS_IN_CHUNKS = SURFACE_SAMPLING_OFFSETS_IN_CHUNKS_PRE_1_19_3;
        }
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void disableSkipSamplingAboveY(NoiseChunk noiseChunk, ChunkPos pos, NoiseRouter router,
                                           PositionalRandomFactory positionalRandomFactory, int minBlockY, int yBlockSize,
                                           Aquifer.FluidPicker globalFluidPicker, CallbackInfo ci) {
        this.skipSamplingAboveY = Integer.MAX_VALUE;
    }
}
