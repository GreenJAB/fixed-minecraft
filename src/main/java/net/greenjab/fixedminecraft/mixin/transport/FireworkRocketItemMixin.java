package net.greenjab.fixedminecraft.mixin.transport;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FireworkRocketItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FireworkRocketItem.class)
public class FireworkRocketItemMixin {

    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    private void removeNormalFireworkElytraUse(World world, PlayerEntity user, Hand hand,
                                               CallbackInfoReturnable<TypedActionResult<ItemStack>> cir) {
        ItemStack itemStack = user.getStackInHand(hand);
        if (itemStack.getItem().equals(Items.FIREWORK_ROCKET)) {
            if (!itemStack.hasNbt()) {
                cir.setReturnValue(TypedActionResult.pass(user.getStackInHand(hand)));
            } else {
                if (!(itemStack.getNbt().toString().contains("Explosions")))
                    cir.setReturnValue(TypedActionResult.pass(user.getStackInHand(hand)));
            }
        }
    }

    /*@Inject(method = "useOnBlock", at = @At("HEAD"),cancellable = true)
    private void injected(ItemUsageContext context, CallbackInfoReturnable cir) {
        ItemStack itemStack = context.getStack();
        FixedMinecraft.LOGGER.info((itemStack.toString())+"");
        //FireworkRocketItem.setFlight(itemStack, (byte) 1);
    }*/
}
