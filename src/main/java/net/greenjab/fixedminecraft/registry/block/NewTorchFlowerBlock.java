package net.greenjab.fixedminecraft.registry.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.FlowerBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCollisionHandler;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public class NewTorchFlowerBlock extends FlowerBlock {

    public NewTorchFlowerBlock(RegistryEntry<StatusEffect> stewEffect, float effectLengthInSeconds, Settings settings) {
        super(stewEffect, effectLengthInSeconds, settings);
    }

    @Override
    protected void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity, EntityCollisionHandler handler, boolean bl) {
        if (entity instanceof LivingEntity && entity.getType() != EntityType.SNIFFER ) {
            if (world instanceof ServerWorld) {
                entity.setFireTicks(100);
            }
        }
    }

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        BlockPos blockPos = pos.up();
        if (world.getBlockState(blockPos).isAir() && !world.getBlockState(blockPos).isOpaqueFullCube()) {
            if (random.nextInt(20) == 0) {
                double d = pos.getX()+0.5;
                double e = pos.getY()+0.5;
                double f = pos.getZ()+0.5;
                double d1 = random.nextDouble();
                double e1 =1.0;
                double f1 = random.nextDouble();
                world.addParticleClient(ParticleTypes.LAVA, d, e, f, d1, e1, f1);
                world.playSoundClient(
                        d, e, f, SoundEvents.BLOCK_FIRE_AMBIENT, SoundCategory.AMBIENT,
                        0.2F + random.nextFloat() * 0.2F, 0.9F + random.nextFloat() * 0.15F, false
                );
            }
        }
    }

}
