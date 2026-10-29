package net.greenjab.fixedminecraft.registry.other;

import net.greenjab.fixedminecraft.registry.ModTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import org.jspecify.annotations.NonNull;

public class BrickEntity extends ThrowableItemProjectile {

    public BrickEntity(Level world, LivingEntity owner, ItemStack stack) {
        super(EntityType.SNOWBALL, owner, world, stack);
    }

    @Override
    protected @NonNull Item getDefaultItem() {
        return Items.AIR;
    }

    private ParticleOptions getParticleParameters() {
        ItemStack itemStack = this.getItem();
        return itemStack.isEmpty() ? ParticleTypes.ITEM_SNOWBALL : new ItemParticleOption(ParticleTypes.ITEM, itemStack.getItem());
    }

    @Override
    public void handleEntityEvent(byte status) {
        if (status == EntityEvent.DEATH) {
            ParticleOptions particleEffect = this.getParticleParameters();

            for (int i = 0; i < 8; i++) {
                this.level().addParticle(particleEffect, this.getX(), this.getY(), this.getZ(), 0.0, 0.0, 0.0);
            }
        }
    }

    @Override
    protected void onHitEntity(@NonNull EntityHitResult entityHitResult) {
        super.onHitEntity(entityHitResult);
        Entity entity = entityHitResult.getEntity();
        entity.hurt(this.damageSources().thrown(this, this.getOwner()), 2f);
    }

    @Override
    protected void onHit(@NonNull HitResult hitResult) {
        super.onHit(hitResult);
        if (!this.level().isClientSide()) {
            this.level().broadcastEntityEvent(this, EntityEvent.DEATH);
            this.discard();
        }
        HitResult.Type type = hitResult.getType();
        if (type == HitResult.Type.BLOCK) {
            BlockHitResult blockHit = (BlockHitResult)hitResult;
            BlockPos target = blockHit.getBlockPos();
            BlockState blockState = this.level().getBlockState(target);
            if (blockState.is(ModTags.GLASS_PANES)) {
                this.level().levelEvent(null, 2001, target, Block.getId(blockState));
                this.level().setBlock(target, Blocks.AIR.defaultBlockState(),3);
                this.level().gameEvent(GameEvent.BLOCK_DESTROY, target, GameEvent.Context.of(this, this.level().getBlockState(target)));
                this.level().playSound(null, target.getX(), target.getY(), target.getZ(), SoundEvents.GLASS_BREAK, SoundSource.BLOCKS);
            }
        }
    }
}
