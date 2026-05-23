package net.greenjab.fixedminecraft.mixin.client.particle;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.ParticleUtils;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.HoneycombItem;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin {

    @Shadow
    @Nullable
    public LocalPlayer player;

    @Shadow
    @Nullable
    public ClientLevel level;

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/ClientLevel;animateTick(III)V"))
    public void addWaxParticles(CallbackInfo ci) {
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
        assert player != null;
        assert level != null;
        if (player.getMainHandItem().is(Items.HONEYCOMB)) {
            for (int j = 0; j < 50; j++) {
                randomWaxedCopperBlockDisplayTick(level, player.getBlockX(), player.getBlockY(), player.getBlockZ(), 8, level.getRandom(), mutable);
            }
        }
    }

    @Unique
    public void randomWaxedCopperBlockDisplayTick(ClientLevel level, int centerX, int centerY, int centerZ, int radius, RandomSource random, BlockPos.MutableBlockPos pos) {
        int i = centerX + random.nextInt(radius) - random.nextInt(radius);
        int j = centerY + random.nextInt(radius) - random.nextInt(radius);
        int k = centerZ + random.nextInt(radius) - random.nextInt(radius);
        pos.set(i, j, k);
        BlockState blockState = level.getBlockState(pos);
        if (HoneycombItem.getWaxed(blockState).isPresent()) {
            for (Direction direction : Direction.values()) {
                ParticleUtils.spawnParticleOnFace(level, pos, direction, ParticleTypes.HAPPY_VILLAGER, new Vec3(0, 0, 0), 0.55);
            }
        }
    }

    @Inject(method = "startAttack", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/player/LocalPlayer;resetAttackStrengthTicker()V"
    ))
    private void reducedCooldownIfMiss(CallbackInfoReturnable<Boolean> cir){
        //TODO miss faster reset
    }
}
