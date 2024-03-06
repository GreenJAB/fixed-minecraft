package net.green_jab.fixed_minecraft.mixin;

import net.green_jab.fixed_minecraft.FixedMinecraft;
import net.green_jab.fixed_minecraft.item.ModItems;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FireworkRocketItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FireworkRocketItem.class)
public class FireworkRocketItemMixin {

    //@Inject(method = "use", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;getStackInHand(I)V", shift = At.Shift.AFTER))
    @Inject(method = "use", at = @At("HEAD"),cancellable = true)
    private void removeNormalFireworkElytraUse(World world, PlayerEntity user, Hand hand, CallbackInfoReturnable cir) {
        ItemStack itemStack = user.getStackInHand(hand);
        if (itemStack.getItem().toString().equals("firework_rocket")) {
            if (!(itemStack.getNbt().toString().contains("Explosions"))) {
                cir.setReturnValue(TypedActionResult.pass(user.getStackInHand(hand)));
            }
        }

        //FixedMinecraft.LOGGER.info((itemStack.getItem().toString())+"");
        //FixedMinecraft.LOGGER.info((itemStack.getNbt().toString().contains("Explosions"))+"");
    }

    /*@Inject(method = "useOnBlock", at = @At("HEAD"),cancellable = true)
    private void injected(ItemUsageContext context, CallbackInfoReturnable cir) {
        ItemStack itemStack = context.getStack();
        FixedMinecraft.LOGGER.info((itemStack.toString())+"");
        //FireworkRocketItem.setFlight(itemStack, (byte) 1);
    }*/
}
