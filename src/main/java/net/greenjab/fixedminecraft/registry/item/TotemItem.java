package net.greenjab.fixedminecraft.registry.item;

import net.greenjab.fixedminecraft.registry.registries.GameruleRegistry;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;

public class TotemItem extends Item {
    public TotemItem(Settings settings) {
        super(settings);
    }

    @Override
    public UseAction getUseAction(ItemStack stack)  {
        return UseAction.TOOT_HORN;
    }
    @Override
    public int getMaxUseTime(ItemStack stack, LivingEntity user) {
        return 72000;
    }
    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand)  {
        if (world != null) {
            if (world instanceof ServerWorld serverWorld) {
                if (serverWorld.getGameRules().getBoolean(GameruleRegistry.Require_Totem_Use)) {
                    user.playSound(SoundEvents.ITEM_SPYGLASS_USE, 1.0f, 1.0f);
                    return ItemUsage.consumeHeldItem(world, user, hand);
                }
            }
        }
        return TypedActionResult.pass(user.getStackInHand(hand));
    }
}
