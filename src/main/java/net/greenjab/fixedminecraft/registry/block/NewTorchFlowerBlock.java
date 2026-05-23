package net.greenjab.fixedminecraft.registry.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.FlowerBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.NonNull;

public class NewTorchFlowerBlock extends FlowerBlock {

    public NewTorchFlowerBlock(Holder<MobEffect> stewEffect, float effectLengthInSeconds, Properties settings) {
        super(stewEffect, effectLengthInSeconds, settings);
    }

    @Override
    protected void entityInside(@NonNull BlockState state, @NonNull Level world, @NonNull BlockPos pos, @NonNull Entity entity, @NonNull InsideBlockEffectApplier handler, boolean bl) {
        if (entity instanceof LivingEntity && entity.getType() != EntityType.SNIFFER ) {
            if (world instanceof ServerLevel) {
                entity.setRemainingFireTicks(100);
            }
        }
    }

    @Override
    public void animateTick(@NonNull BlockState state, Level world, BlockPos pos, @NonNull RandomSource random) {
        BlockPos blockPos = pos.above();
        if (world.getBlockState(blockPos).isAir() && !world.getBlockState(blockPos).isSolidRender()) {
            if (random.nextInt(20) == 0) {
                double d = pos.getX()+0.5;
                double e = pos.getY()+0.5;
                double f = pos.getZ()+0.5;
                double d1 = random.nextDouble();
                double e1 =1.0;
                double f1 = random.nextDouble();
                world.addParticle(ParticleTypes.LAVA, d, e, f, d1, e1, f1);
                world.playLocalSound(
                        d, e, f, SoundEvents.FIRE_AMBIENT, SoundSource.AMBIENT,
                        0.2F + random.nextFloat() * 0.2F, 0.9F + random.nextFloat() * 0.15F, false
                );
            }
        }
    }

}
