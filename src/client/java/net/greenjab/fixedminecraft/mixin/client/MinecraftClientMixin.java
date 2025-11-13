package net.greenjab.fixedminecraft.mixin.client;

import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.HoneycombItem;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.particle.ParticleUtil;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin( MinecraftClient.class)
public class MinecraftClientMixin {

    @Shadow
    @Nullable
    public ClientPlayerEntity player;

    @Shadow
    @Nullable
    public ClientWorld world;

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/world/ClientWorld;doRandomBlockDisplayTicks(III)V"))
    public void addWaxParticles(CallbackInfo ci) {
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        assert player != null;
        assert world != null;
        if (player.getMainHandStack().isOf(Items.HONEYCOMB)) {
            for (int j = 0; j < 50; j++) {
                randomWaxedCopperBlockDisplayTick(world, player.getBlockX(), player.getBlockY(), player.getBlockZ(), 8, world.random, mutable);
            }
        }
    }

    @Unique
    public void randomWaxedCopperBlockDisplayTick(ClientWorld world, int centerX, int centerY, int centerZ, int radius, Random random, BlockPos.Mutable pos) {
        int i = centerX + random.nextInt(radius) - random.nextInt(radius);
        int j = centerY + random.nextInt(radius) - random.nextInt(radius);
        int k = centerZ + random.nextInt(radius) - random.nextInt(radius);
        pos.set(i, j, k);
        BlockState blockState = world.getBlockState(pos);
        if (HoneycombItem.getWaxedState(blockState).isPresent()) {
            for (Direction direction : Direction.values()) {
                ParticleUtil.spawnParticles(world, pos, ParticleTypes.HAPPY_VILLAGER, UniformIntProvider.create(3, 3), direction, () -> new Vec3d(0, 0, 0), 0.55);
            }
        }

    }

}
