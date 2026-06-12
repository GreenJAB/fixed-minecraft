package net.greenjab.fixedminecraft.mixin.enchanting;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
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
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collections;

@Mixin(GrindstoneMenu.class)
public abstract class GrindstoneMenuMixin extends AbstractContainerMenu {

    @Shadow
    protected abstract ItemStack computeResult(ItemStack input, ItemStack additional);

    public GrindstoneMenuMixin(
            @Nullable MenuType<?> type, int syncId) {
        super(type, syncId);
    }

    @Redirect(method = "createResult", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/inventory/GrindstoneMenu;computeResult(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;)Lnet/minecraft/world/item/ItemStack;"))
    private ItemStack damageGrindstonedItem(GrindstoneMenu instance, ItemStack input, ItemStack additional) {
        if (input.isEmpty() || additional.isEmpty()) {
            ItemStack original = computeResult(input, additional);
            if (original.is(Items.BOOK) || original.is(Items.ENCHANTED_BOOK)) return original;
            boolean bl4 = !input.isEmpty();
            if (bl4) {
                int max = input.getMaxDamage();
                ItemStack output = input.copy();
                output.setDamageValue(input.getDamageValue() + Mth.ceil(max / 4f));
                if (input.getDamageValue() + max / 4 >= max) {
                    output.set(DataComponents.REPAIR_COST, 5);
                    output.set(DataComponents.LORE, new ItemLore(Collections.singletonList(Component.translatable("container.grindstone.break_item"))));
                }
                return computeResult(output, additional);
            } else {
                int max = additional.getMaxDamage();
                ItemStack output = additional.copy();
                output.setDamageValue(additional.getDamageValue() + Mth.ceil(max / 4f));
                if (input.getDamageValue() + max / 4 >= max) {
                    output.set(DataComponents.REPAIR_COST, 5);
                    output.set(DataComponents.LORE, new ItemLore(Collections.singletonList(Component.translatable("container.grindstone.break_item"))));
                }
                return computeResult(input, output);
            }
        } else {
            return computeResult(input, additional);
        }
    }

    @Inject(method = "removeNonCursesFrom", at = @At(value = "INVOKE", target ="Lnet/minecraft/world/item/ItemStack;set(Lnet/minecraft/core/component/DataComponentType;Ljava/lang/Object;)Ljava/lang/Object;"),
            cancellable = true)
    private void dontModifyRepairCost(ItemStack item, CallbackInfoReturnable<ItemStack> cir) {
        cir.setReturnValue(item);
    }


    @WrapOperation(method = "quickMoveStack", at = @At(value = "INVOKE", target ="Lnet/minecraft/world/inventory/GrindstoneMenu;moveItemStackTo(Lnet/minecraft/world/item/ItemStack;IIZ)Z", ordinal = 0))
    private boolean destroy0DurabilityItem(GrindstoneMenu instance, ItemStack itemStack, int i, int j, boolean b, Operation<Boolean> original) {
        return true;
    }

    @Inject(method = "quickMoveStack", at = @At(value = "INVOKE", target ="Lnet/minecraft/world/inventory/Slot;onQuickCraft(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;)V"),
            cancellable = true
    )
    private void destroy0DurabilityItem2(Player player, int slotIndex, CallbackInfoReturnable<ItemStack> cir, @Local(ordinal = 1) ItemStack item) {
        if (item.getComponents().getOrDefault(DataComponents.REPAIR_COST, 0) == 5){
            this.clicked(2, 1, ContainerInput.PICKUP, player);
            cir.setReturnValue(ItemStack.EMPTY);
        }
    }

}
