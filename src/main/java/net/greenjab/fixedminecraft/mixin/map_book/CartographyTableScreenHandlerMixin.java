package net.greenjab.fixedminecraft.mixin.map_book;

import net.greenjab.fixedminecraft.registry.registries.ItemRegistry;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.CartographyTableScreenHandler;
import net.minecraft.screen.slot.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(CartographyTableScreenHandler.class)
public abstract class CartographyTableScreenHandlerMixin {

    @ModifyArg(method = "<init>(ILnet/minecraft/entity/player/PlayerInventory;Lnet/minecraft/screen/ScreenHandlerContext;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/screen/CartographyTableScreenHandler;addSlot(Lnet/minecraft/screen/slot/Slot;)Lnet/minecraft/screen/slot/Slot;", ordinal = 0), index = 0)
    private Slot notInfEffect(Slot par1){
        CartographyTableScreenHandler CTSH = (CartographyTableScreenHandler)(Object)this;
        return new Slot(CTSH.inventory, 0, 15, 15) /* CartographyTableScreenHandler$3 */ {
            @Override
            public boolean canInsert(ItemStack stack) {
                return stack.contains(DataComponentTypes.MAP_ID) && !stack.isOf(ItemRegistry.MAP_BOOK);
            }
        };
    }

}
