package net.greenjab.fixedminecraft.mixin.minecart;

import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;

//@Mixin(AxeItem.class)
@Mixin(Player.class)
public abstract class AxeItemMixin {
    //TODO scraped copper
    /*@Inject(method = "evaluateNewBlockState", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/item/AxeItem;spawnSoundAndParticle(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/sounds/SoundEvent;I)V", ordinal = 0
    ))
    private void addScrapedCopper(Level level, BlockPos pos, @Nullable Player player,
                                  BlockState oldState, CallbackInfoReturnable<Optional<BlockState>> cir) {
        if (level instanceof ServerLevel serverWorld && oldState.isCollisionShapeFullBlock(level, pos) && level.getRandom().nextFloat() < 0.3f) {
            Identifier lootTableId = FixedMinecraft.id("gameplay/other/scrape");
            Block.dropFromBlockInteractLootTable(
                    serverWorld,
                    ResourceKey.create(Registries.LOOT_TABLE, lootTableId),
                    oldState,
                    level.getBlockEntity(pos),
                    null,
                    player,
                     (worldx, stack) -> Block.popResource(worldx, pos, stack)
            );
        }
    }*/
}
