package net.greenjab.fixedminecraft.mixin.mobs;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.greenjab.fixedminecraft.registry.registries.GameRuleRegistry;
import net.greenjab.fixedminecraft.registry.registries.ItemRegistry;
import net.minecraft.advancements.triggers.CriteriaTriggers;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.Bucketable;
import net.minecraft.world.entity.animal.allay.Allay;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gamerules.GameRule;
import org.jspecify.annotations.NonNull;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(Allay.class)
public abstract class AllayMixin extends PathfinderMob implements Bucketable {
    @Shadow
    public abstract void dropEquipment(@NonNull ServerLevel level);

    public AllayMixin(EntityType<? extends PathfinderMob> type, Level level) {
        super(type, level);
    }

    @ModifyExpressionValue(method = "wantsToPickUp", at = @At(value = "FIELD", target = "Lnet/minecraft/world/level/gamerules/GameRules;MOB_GRIEFING:Lnet/minecraft/world/level/gamerules/GameRule;", opcode = Opcodes.GETSTATIC))
    public GameRule<Boolean> passiveMobGriefing(GameRule<Boolean> original) {
        return GameRuleRegistry.PEACEFUL_MOB_GRIEFING;
    }

    @Inject(method = "mobInteract", at = @At(value = "HEAD"), cancellable = true)
    public void bucket(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        if (!player.isCrouching()) bucketMobPickup(player, hand, this).ifPresent(cir::setReturnValue);
    }

    @Unique
    private static <T extends LivingEntity & Bucketable> Optional<InteractionResult> bucketMobPickup(final Player player, final InteractionHand hand, final T pickupEntity) {
        ItemStack itemStack = player.getItemInHand(hand);
        if (itemStack.getItem() == Items.BUCKET && pickupEntity.isAlive()) {
            pickupEntity.playSound((pickupEntity).getPickupSound(), 1.0F, 1.0F);
            ItemStack bucket = (pickupEntity).getBucketItemStack();
            (pickupEntity).saveToBucketTag(bucket);
            ItemStack result = ItemUtils.createFilledResult(itemStack, player, bucket, false);
            player.setItemInHand(hand, result);
            Level level = pickupEntity.level();
            if (!level.isClientSide()) {
                CriteriaTriggers.FILLED_BUCKET.trigger((ServerPlayer) player, bucket);
                ((Allay) pickupEntity).dropEquipment((ServerLevel)level);
            }
            pickupEntity.discard();
            return Optional.of(InteractionResult.SUCCESS);
        } else return Optional.empty();
    }

    @Override public void setFromBucket(final boolean fromBucket) {}
    @Override public void saveToBucketTag(final @NonNull ItemStack bucket) {Bucketable.saveDefaultDataToBucketTag(this, bucket);}
    @Override public void loadFromBucketTag(final @NonNull CompoundTag tag) {Bucketable.loadDefaultDataFromBucketTag(this, tag);}
    @Override public @NonNull ItemStack getBucketItemStack() {return new ItemStack(ItemRegistry.ALLAY_BUCKET);}
    @Override public @NonNull SoundEvent getPickupSound() {return SoundEvents.BUCKET_FILL_AXOLOTL;}
}

