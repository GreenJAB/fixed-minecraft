package net.greenjab.fixedminecraft.mixin.transport;

import net.greenjab.fixedminecraft.registry.registries.ItemRegistry;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.FireworkRocketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.Fireworks;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FireworkRocketItem.class)
public abstract class FireworkRocketItemMixin {

    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    private void removeNormalFireworkElytraUse(Level level, Player player, InteractionHand hand,
                                               CallbackInfoReturnable<InteractionResult> cir) {
        ItemStack itemStack = player.getItemInHand(hand);
        if (itemStack.getItem().equals(Items.FIREWORK_ROCKET) && itemStack.getComponents().has(DataComponents.FIREWORKS)) {
            Fireworks fireworkComponent = itemStack.get(DataComponents.FIREWORKS);
            if (fireworkComponent == null) {
                if (player.isFallFlying()) {
                    player.push(0, 1, 0);
                    if (level instanceof ServerLevel serverWorld) {
                        if (player.dropAllLeashConnections(null)) {
                            level.playSound(null, player, SoundEvents.LEAD_BREAK, SoundSource.NEUTRAL, 1.0F, 1.0F);
                        }
                        Projectile.spawnProjectile(
                                new FireworkRocketEntity(
                                        level,
                                        player.getX(),
                                        player.getY(),
                                        player.getZ(),
                                        itemStack
                                ),
                                serverWorld,
                                itemStack
                        );
                        itemStack.consume(1, player);
                        player.awardStat(Stats.ITEM_USED.get((FireworkRocketItem)(Object)this));
                    }

                    cir.setReturnValue(InteractionResult.SUCCESS);
                }
            }
            if (fireworkComponent.explosions().isEmpty()) {
                if (player.isFallFlying()) {
                    player.push(0, 1, 0);
                    if (level instanceof ServerLevel serverWorld) {
                        if (player.dropAllLeashConnections(null)) {
                            level.playSound(null, player, SoundEvents.LEAD_BREAK, SoundSource.NEUTRAL, 1.0F, 1.0F);
                        }
                        LivingEntity Null = EntityType.PIG.create(level, EntitySpawnReason.TRIGGERED);
                        if (Null != null) {
                            Null.snapTo(player.getX(), player.getY(), player.getZ(), 0, 0.0F);
                            Projectile.spawnProjectile(new FireworkRocketEntity(level, itemStack, Null), serverWorld, itemStack);
                            Null.remove(Entity.RemovalReason.DISCARDED);
                        }
                        itemStack.consume(1, player);
                        player.awardStat(Stats.ITEM_USED.get((FireworkRocketItem)(Object)this));
                    }

                    cir.setReturnValue(InteractionResult.SUCCESS);
                }
            }
        }
        if (player.isFallFlying()) {
            if (player instanceof ServerPlayer SPE && itemStack.getItem().equals(ItemRegistry.DRAGON_FIREWORK_ROCKET)) {
                CriteriaTriggers.CONSUME_ITEM.trigger(SPE, itemStack);
            }
        }
    }

    @Inject(method = "useOn", at = @At("HEAD"),cancellable = true)
    private void cantUseDragonRocketsOnGround(UseOnContext context, CallbackInfoReturnable<InteractionResult> cir) {
        if (context.getItemInHand().getItem().equals(ItemRegistry.DRAGON_FIREWORK_ROCKET)) cir.setReturnValue(InteractionResult.PASS);
    }
}
