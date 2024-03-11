package net.greenjab.fixedminecraft.mixin;

import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.StewItem;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.Stats;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(StewItem.class)
public class StewItemMixin {

    @Inject(method = "finishUsing", at = @At("HEAD"), cancellable = true)
    private void fixStackedBowlUse(ItemStack stack, World world, LivingEntity user, CallbackInfoReturnable cir) {
        //ItemStack itemStack = stack.getItem().finishUsing(stack, world, user);
        /*if (user instanceof ServerPlayerEntity serverPlayerEntity) {
            Criteria.CONSUME_ITEM.trigger(serverPlayerEntity, itemStack);
            serverPlayerEntity.incrementStat(Stats.USED.getOrCreateStat((StewItem)(Object)this));
        }

        if (itemStack.isEmpty()) {
            cir.setReturnValue( new ItemStack(Items.BOWL));
        } else {
            if (user instanceof PlayerEntity) {
                PlayerEntity playerEntity = (PlayerEntity)user;
                if (!playerEntity.getAbilities().creativeMode) {
                    ItemStack itemStack2 = new ItemStack(Items.BOWL);
                    if (!playerEntity.getInventory().insertStack(itemStack)) {
                        playerEntity.dropItem(itemStack2, false);
                    }
                }
            }

            cir.setReturnValue(stack);
        }*/
    }
}
