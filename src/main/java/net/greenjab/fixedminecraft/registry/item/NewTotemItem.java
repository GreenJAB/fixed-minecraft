package net.greenjab.fixedminecraft.registry.item;

import net.greenjab.fixedminecraft.registry.registries.GameRuleRegistry;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.NonNull;

public class NewTotemItem extends Item {
    public NewTotemItem(Properties settings) {
        super(settings);
    }

    @Override
    public @NonNull ItemUseAnimation getUseAnimation(@NonNull ItemStack stack)  {
        return ItemUseAnimation.TOOT_HORN;
    }
    @Override
    public int getUseDuration(@NonNull ItemStack stack, @NonNull LivingEntity user) {
        return 72000;
    }
    @Override
    public @NonNull InteractionResult use(@NonNull Level world, @NonNull Player user, @NonNull InteractionHand hand)  {
        if (world instanceof ServerLevel serverWorld) {
            if (serverWorld.getGameRules().get(GameRuleRegistry.REQUIRE_TOTEM_USE)) {
                user.playSound(SoundEvents.SPYGLASS_USE, 1.0f, 1.0f);
                return ItemUtils.startUsingInstantly(world, user, hand);
            }
        }
        return InteractionResult.PASS;
    }
}
