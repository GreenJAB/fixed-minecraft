package net.greenjab.fixedminecraft.mixin.mobs;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.ShulkerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.DyeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import java.util.Optional;

@Mixin(DyeItem.class)
public class DyeItemMixin {

    @Shadow
    @Final
    private DyeColor color;

    @Inject(method = "useOnEntity", at = @At("HEAD"), cancellable = true)
    private void dyeableShulkerEntities(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand,
                                        CallbackInfoReturnable<ActionResult> cir) {
        if (entity instanceof ShulkerEntity shulkerEntity && shulkerEntity.isAlive() && shulkerEntity.getColor() != this.color) {
            shulkerEntity.getWorld().playSoundFromEntity(user, shulkerEntity, SoundEvents.ITEM_DYE_USE, SoundCategory.PLAYERS, 1.0F, 1.0F);
            if (!user.getWorld().isClient()) {
                shulkerEntity.setVariant(Optional.ofNullable(color));
                stack.decrement(1);
            }
            cir.setReturnValue(ActionResult.SUCCESS);
        }
    }
}
