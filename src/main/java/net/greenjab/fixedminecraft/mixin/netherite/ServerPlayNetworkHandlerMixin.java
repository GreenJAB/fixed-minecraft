package net.greenjab.fixedminecraft.mixin.netherite;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ServerPlayNetworkHandler.class)
public class ServerPlayNetworkHandlerMixin {

    @Redirect(method = "onPlayerAction", at = @At(value = "INVOKE",
                                                  target = "Lnet/minecraft/server/network/ServerPlayerEntity;getStackInHand(Lnet/minecraft/util/Hand;)Lnet/minecraft/item/ItemStack;"
    ))
    private ItemStack noNetheriteFix3(ServerPlayerEntity instance, Hand hand) {
        if (hand == Hand.MAIN_HAND) {
            return instance.equipment.get(EquipmentSlot.MAINHAND);
        }
        else if (hand == Hand.OFF_HAND) {
            return instance.equipment.get(EquipmentSlot.OFFHAND);
        }
        else {
            throw new IllegalArgumentException("Invalid hand " + hand);
        }
    }
}
