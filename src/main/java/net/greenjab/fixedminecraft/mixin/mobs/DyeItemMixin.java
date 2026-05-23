package net.greenjab.fixedminecraft.mixin.mobs;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.DyeColor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import java.util.Optional;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Shulker;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;

@Mixin(DyeItem.class)
public abstract class DyeItemMixin {

    @Inject(method = "interactLivingEntity", at = @At("HEAD"), cancellable = true)
    private void dyeableShulkerEntities(ItemStack itemStack, Player player, LivingEntity target, InteractionHand type,
                                        CallbackInfoReturnable<InteractionResult> cir) {
        DyeColor dyeColor = itemStack.get(DataComponents.DYE);
        if (target instanceof Shulker shulkerEntity && shulkerEntity.isAlive() && shulkerEntity.getColor() != dyeColor) {
            shulkerEntity.level().playSound(player, shulkerEntity, SoundEvents.DYE_USE, SoundSource.PLAYERS, 1.0F, 1.0F);
            if (!player.level().isClientSide()) {
                shulkerEntity.setVariant(Optional.ofNullable(dyeColor));
                itemStack.shrink(1);
            }
            cir.setReturnValue(InteractionResult.SUCCESS);
        }
    }
}
