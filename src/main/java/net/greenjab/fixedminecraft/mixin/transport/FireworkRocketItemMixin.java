package net.greenjab.fixedminecraft.mixin.transport;

import net.greenjab.fixedminecraft.registry.registries.ItemRegistry;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.FireworksComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.FireworkRocketItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FireworkRocketItem.class)
public class FireworkRocketItemMixin {

    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    private void removeNormalFireworkElytraUse(World world, PlayerEntity user, Hand hand,
                                               CallbackInfoReturnable<TypedActionResult<ItemStack>> cir) {
        ItemStack itemStack = user.getStackInHand(hand);
        if (itemStack.getItem().equals(Items.FIREWORK_ROCKET)) {
            FireworksComponent fireworkComponent = itemStack.get(DataComponentTypes.FIREWORKS);
            if (fireworkComponent == null || fireworkComponent.explosions().isEmpty()) {
                if (user.isFallFlying()) {
                    user.addVelocity(0, 1, 0);
                    if (world instanceof ServerWorld serverWorld) {
                        user.getItemCooldownManager().set(itemStack.getItem(),100);
                        FireworkRocketEntity fireworkRocketEntity = new FireworkRocketEntity(world,  user.getX(),
                                user.getY(),
                                user.getZ(),itemStack);
                        world.spawnEntity(fireworkRocketEntity);

                        itemStack.decrementUnlessCreative(1, user);
                        user.incrementStat(Stats.USED.getOrCreateStat((FireworkRocketItem)(Object)this));
                    }
                    cir.setReturnValue(TypedActionResult.success(user.getStackInHand(hand), world.isClient()));
                }
            }
        }
        if (user.isFallFlying()) {
            if (user instanceof ServerPlayerEntity SPE && itemStack.getItem().equals(ItemRegistry.DRAGON_FIREWORK_ROCKET)) {
                Criteria.CONSUME_ITEM.trigger(SPE, itemStack);
            }
        }
    }

    @Inject(method = "useOnBlock", at = @At("HEAD"),cancellable = true)
    private void injected(ItemUsageContext context, CallbackInfoReturnable<ActionResult> cir) {
        if (context.getStack().getItem().equals(ItemRegistry.DRAGON_FIREWORK_ROCKET)) cir.setReturnValue(ActionResult.PASS);
    }
}
