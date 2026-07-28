package net.greenjab.fixedminecraft.mixin.enchanting;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.greenjab.fixedminecraft.FixedMinecraft;
import net.greenjab.fixedminecraft.registry.registries.GameRuleRegistry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.GrindstoneMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ItemLore;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collections;

@Mixin(GrindstoneMenu.class)
public abstract class GrindstoneMenuMixin extends AbstractContainerMenu {

    public GrindstoneMenuMixin(@Nullable MenuType<?> type, int syncId) {
        super(type, syncId);
    }

    @WrapOperation(method = "createResult", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/inventory/GrindstoneMenu;computeResult(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;)Lnet/minecraft/world/item/ItemStack;"))
    private ItemStack damageGrindstonedItem(GrindstoneMenu instance, ItemStack input, ItemStack additional, Operation<ItemStack> original) {
        ItemStack originalItem = original.call(instance, input, additional);
        if (input.isEmpty() || additional.isEmpty()) {
            if (!FixedMinecraft.SERVER.getGameRules().get(GameRuleRegistry.GRINDSTONE_DAMAGES_ITEM)) return originalItem;
            if (originalItem.is(Items.BOOK) || originalItem.is(Items.ENCHANTED_BOOK)) return originalItem;
            boolean bl4 = !input.isEmpty();
            if (bl4) {
                int max = input.getMaxDamage();
                ItemStack output = input.copy();
                output.setDamageValue(input.getDamageValue() + Mth.ceil(max / 4f));
                if (input.getDamageValue() + max / 4 >= max) {
                    output.set(DataComponents.REPAIR_COST, 5);
                    output.set(DataComponents.LORE, new ItemLore(Collections.singletonList(Component.translatable("container.grindstone.break_item"))));
                }
                return original.call(instance,output, additional);
            } else {
                int max = additional.getMaxDamage();
                ItemStack output = additional.copy();
                output.setDamageValue(additional.getDamageValue() + Mth.ceil(max / 4f));
                if (input.getDamageValue() + max / 4 >= max) {
                    output.set(DataComponents.REPAIR_COST, 5);
                    output.set(DataComponents.LORE, new ItemLore(Collections.singletonList(Component.translatable("container.grindstone.break_item"))));
                }
                return original.call(instance,input, output);
            }
        } return originalItem;
    }

    @Inject(method = "removeNonCursesFrom", at = @At(value = "INVOKE", target ="Lnet/minecraft/world/item/ItemStack;set(Lnet/minecraft/core/component/DataComponentType;Ljava/lang/Object;)Ljava/lang/Object;"), cancellable = true)
    private void dontModifyRepairCost(ItemStack item, CallbackInfoReturnable<ItemStack> cir) {
        cir.setReturnValue(item);
    }


    @WrapOperation(method = "quickMoveStack", at = @At(value = "INVOKE", target ="Lnet/minecraft/world/inventory/GrindstoneMenu;moveItemStackTo(Lnet/minecraft/world/item/ItemStack;IIZ)Z", ordinal = 0))
    private boolean destroy0DurabilityItem(GrindstoneMenu instance, ItemStack itemStack, int i, int j, boolean b, Operation<Boolean> original) {
        return true;
    }

    @Inject(method = "quickMoveStack", at = @At(value = "INVOKE", target ="Lnet/minecraft/world/inventory/Slot;onQuickCraft(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;)V"), cancellable = true)
    private void destroy0DurabilityItem2(Player player, int slotIndex, CallbackInfoReturnable<ItemStack> cir, @Local(ordinal = 1) ItemStack item) {
        if (item.getComponents().getOrDefault(DataComponents.REPAIR_COST, 0) == 5){
            this.clicked(2, 1, ContainerInput.PICKUP, player);
            cir.setReturnValue(ItemStack.EMPTY);
        }
    }
}
