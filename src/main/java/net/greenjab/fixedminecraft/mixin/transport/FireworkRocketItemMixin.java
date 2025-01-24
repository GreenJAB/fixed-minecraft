package net.greenjab.fixedminecraft.mixin.transport;

import net.greenjab.fixedminecraft.registry.ItemRegistry;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FireworkRocketItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FireworkRocketItem.class)
public class FireworkRocketItemMixin {

    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    private void removeNormalFireworkElytraUse(World world, PlayerEntity user, Hand hand,
                                               CallbackInfoReturnable<ActionResult> cir) {
        ItemStack itemStack = user.getStackInHand(hand);
        if (itemStack.getItem().equals(Items.FIREWORK_ROCKET)) {
            if (!itemStack.getComponents().contains(DataComponentTypes.FIREWORK_EXPLOSION)) {
                cir.setReturnValue(ActionResult.PASS);
            }
        }
        if (user.isGliding()) {
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
