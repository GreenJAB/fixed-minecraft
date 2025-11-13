package net.greenjab.fixedminecraft.registry.item;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;

public class GlisteringMelonSliceItem extends Item {
    public GlisteringMelonSliceItem(Settings settings) {
        super(settings);
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        super.finishUsing(stack, world, user);
        if (user instanceof ServerPlayerEntity serverPlayerEntity) {
            serverPlayerEntity.setHealth(user.getHealth()+2);
        }
        return stack;
    }
}
