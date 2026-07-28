package net.greenjab.fixedminecraft.mixin.netherite;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ServerGamePacketListenerImpl.class)
public abstract class ServerGamePacketListenerImplMixin {

    @WrapOperation(method = "handlePlayerAction", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;getItemInHand(Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/item/ItemStack;"))
    private ItemStack noNetheriteFix3(ServerPlayer instance, InteractionHand interactionHand, Operation<ItemStack> original) {
        if (interactionHand == InteractionHand.MAIN_HAND) return instance.equipment.get(EquipmentSlot.MAINHAND);
        else if (interactionHand == InteractionHand.OFF_HAND) return instance.equipment.get(EquipmentSlot.OFFHAND);
        else return original.call(instance, interactionHand);
    }
}
