package net.greenjab.fixedminecraft.mixin.other;

import net.greenjab.fixedminecraft.registry.registries.GameRuleRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.game.ClientboundLevelParticlesPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
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
            if (level.getBrightness(LightLayer.BLOCK, pos) > 11 - state.getLightDampening() || level.environmentAttributes().getValue(EnvironmentAttributes.WATER_EVAPORATES, pos)) {
                if (notNextToCryingObsidian(level, pos)) {
                    this.melt(state, level, pos);
                    level.playSound(null, pos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.5F, 2.6F + (random.nextFloat() - random.nextFloat()) * 0.8F);
                    for (int i = 0; i < 8; i++) {
                        level.addParticle(ParticleTypes.LARGE_SMOKE, pos.getX() + random.nextFloat(), pos.getY() + random.nextFloat(), pos.getZ() + random.nextFloat(), 0.0, 0.0, 0.0);
                    }
                }
            }
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
