package net.greenjab.fixedminecraft.mixin.mobs;

import com.llamalad7.mixinextras.sugar.Local;
import net.greenjab.fixedminecraft.registry.registries.ItemRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.MobBucketItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BucketItem.class)
public abstract class BucketItemMixin {

    @Inject(method = "use", at = @At(value = "FIELD", target = "Lnet/minecraft/world/InteractionResult;FAIL:Lnet/minecraft/world/InteractionResult$Fail;", ordinal = 1, opcode = Opcodes.GETSTATIC), cancellable = true)
    public void bucket(Level level, Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir, @Local(ordinal = 0) ItemStack itemStack, @Local(ordinal = 1) BlockPos directionOffsetPos) {
        if (itemStack.is(ItemRegistry.ALLAY_BUCKET)) {
            if (level instanceof ServerLevel && itemStack.getItem() instanceof MobBucketItem bucketItem) {
                bucketItem.spawn((ServerLevel)level, itemStack, directionOffsetPos);
                level.gameEvent(player, GameEvent.ENTITY_PLACE, directionOffsetPos);
            }
            ItemStack emptyResult = ItemUtils.createFilledResult(itemStack, player, BucketItem.getEmptySuccessItem(itemStack, player));
            cir.setReturnValue( InteractionResult.SUCCESS.heldItemTransformedTo(emptyResult));
        }
    }
}

