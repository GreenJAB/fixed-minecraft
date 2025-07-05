package net.greenjab.fixedminecraft.mixin.food;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ConsumableComponent;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public class ItemMixin {

    @Inject(method = "getMaxUseTime", at = @At("HEAD"), cancellable = true)
    private void modifyFoodEatTimes(ItemStack stack, LivingEntity user, CallbackInfoReturnable<Integer> cir) {
        if (stack.getItem().getComponents().contains(DataComponentTypes.FOOD)) {
            cir.setReturnValue(10 + 6 * stack.getItem().getComponents().get(DataComponentTypes.FOOD).nutrition());
            if (stack.isIn(ItemTags.PIGLIN_LOVED)) cir.setReturnValue(60);
        }
    }

    @Inject(method = "use", at = @At("HEAD"))
    private void rawFoodDebuf(World world, PlayerEntity user, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        ItemStack itemStack = user.getStackInHand(hand);

        FoodComponent foodComponent = itemStack.get(DataComponentTypes.FOOD);
        if (foodComponent != null) {
            if (foodComponent.saturation()/(foodComponent.nutrition()*2.0f)==0.15f) {
                if (Math.random() < 0.15f) {
                    user.addStatusEffect(new StatusEffectInstance(StatusEffects.HUNGER, 600, 0));
                }
            }
        }
    }
}
