package net.greenjab.fixedminecraft.mixin.structure;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.structures.WoodlandMansionPieces;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WoodlandMansionPieces.WoodlandMansionPiece.class)
public abstract class WoodlandMansionPieceMixin {

    @Inject(method = "handleDataMarker", at = @At(value = "HEAD"), cancellable = true)
    private void spawnMoreIllagers(String markerId, BlockPos position, ServerLevelAccessor level, RandomSource random,
                                                BoundingBox chunkBB, CallbackInfo ci) {
        if (markerId.startsWith("Illager")) {
            Mob mob;
            int i = random.nextInt(20);
            if (i < 1) mob = EntityType.EVOKER.create(level.getLevel(), EntitySpawnReason.STRUCTURE);
            else if (i < 4) mob = EntityType.WITCH.create(level.getLevel(), EntitySpawnReason.STRUCTURE);
            else if (i < 8) mob = EntityType.ILLUSIONER.create(level.getLevel(), EntitySpawnReason.STRUCTURE);
            else if (i < 12) mob = EntityType.VINDICATOR.create(level.getLevel(), EntitySpawnReason.STRUCTURE);
            else mob = EntityType.PILLAGER.create(level.getLevel(), EntitySpawnReason.STRUCTURE);
            if (mob != null) {
                mob.setPersistenceRequired();
                mob.snapTo(position, 0.0F, 0.0F);
                mob.finalizeSpawn(level, level.getCurrentDifficultyAt(mob.blockPosition()), EntitySpawnReason.STRUCTURE, null);
                level.addFreshEntityWithPassengers(mob);
                level.setBlock(position, Blocks.AIR.defaultBlockState(), 2);
                ci.cancel();
            }
        }
    }
}
