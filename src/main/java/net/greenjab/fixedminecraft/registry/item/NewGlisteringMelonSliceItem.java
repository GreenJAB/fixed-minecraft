package net.greenjab.fixedminecraft.registry.item;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.NonNull;

public class NewGlisteringMelonSliceItem extends Item {
    public NewGlisteringMelonSliceItem(Properties settings) {
        super(settings);
    }

    @Override
    public @NonNull ItemStack finishUsingItem(@NonNull ItemStack stack, @NonNull Level world, @NonNull LivingEntity user) {
        super.finishUsingItem(stack, world, user);
        if (user instanceof ServerPlayer serverPlayerEntity) {
            serverPlayerEntity.setHealth(user.getHealth()+2);
        }
        return stack;
    }
}
