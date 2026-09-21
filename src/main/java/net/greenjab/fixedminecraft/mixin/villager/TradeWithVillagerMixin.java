package net.greenjab.fixedminecraft.mixin.villager;

import com.google.common.collect.ImmutableMap;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.behavior.TradeWithVillager;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import java.util.Map;
import java.util.function.Predicate;

@Mixin(TradeWithVillager.class)
public abstract class TradeWithVillagerMixin  {

    @Unique private static final Map<Object, Object> FOOD_POINTS = ImmutableMap.builder().put(Items.APPLE, 4).put(Items.MELON_SLICE, 2).put(Items.SWEET_BERRIES, 2).put(Items.GLOW_BERRIES, 2)
            .put(Items.CARROT, 3).put(Items.POTATO, 2).put(Items.BAKED_POTATO, 5).put(Items.BEETROOT, 2).put(Items.DRIED_KELP, 1)
            .put(Items.BREAD, 5).put(Items.COOKIE, 2).put(Items.PUMPKIN_PIE, 8).put(Items.MUSHROOM_STEW, 6).put(Items.BEETROOT_SOUP, 6)
            .put(Items.HONEY_BOTTLE, 3).build();

    @WrapOperation(method = "tick(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/npc/villager/Villager;J)V", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/ai/behavior/TradeWithVillager;throwHalfStack(Lnet/minecraft/world/entity/npc/villager/Villager;Ljava/util/function/Predicate;Lnet/minecraft/world/entity/LivingEntity;)V", ordinal = 0
    ))
    private void revertToOldVersion2(Villager villager, Predicate<ItemStack> predicate, LivingEntity target, Operation<Void> original) {
        throwHalfStack(villager, itemStack -> FOOD_POINTS.containsKey(itemStack.getItem()), target);
    }

    @Unique
    private static void throwHalfStack(final Villager villager, final Predicate<ItemStack> predicate, final LivingEntity target) {
        SimpleContainer inventory = villager.getInventory();
        ItemStack toThrow = ItemStack.EMPTY;
        int i = 0;

        while (i < inventory.getContainerSize()) {
            ItemStack itemStack;
            int count;
            label28: {
                itemStack = inventory.getItem(i);
                if (!itemStack.isEmpty() && predicate.test(itemStack)) {
                    if (itemStack.getCount() > itemStack.getMaxStackSize() / 2) {
                        count = itemStack.getCount() / 2;
                        break label28;
                    }

                    if (itemStack.getCount() > 24) {
                        count = itemStack.getCount() - 24;
                        break label28;
                    }
                }

                i++;
                continue;
            }

            toThrow = itemStack.split(count);
            break;
        }

        if (!toThrow.isEmpty()) {
            BehaviorUtils.throwItem(villager, toThrow, target.position());
        }
    }
}
