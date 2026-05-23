package net.greenjab.fixedminecraft.mixin.netherite;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ServerGamePacketListenerImpl.class)
public abstract class ServerGamePacketListenerImplMixin {

    @Redirect(method = "handlePlayerAction", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/server/level/ServerPlayer;getItemInHand(Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/item/ItemStack;"
    ))
    private ItemStack noNetheriteFix3(ServerPlayer instance, InteractionHand hand) {
        if (hand == InteractionHand.MAIN_HAND) {
            return instance.equipment.get(EquipmentSlot.MAINHAND);
        }
        else if (hand == InteractionHand.OFF_HAND) {
            return instance.equipment.get(EquipmentSlot.OFFHAND);
        }
        else {
            throw new IllegalArgumentException("Invalid hand " + hand);
        }
    }
}
