package net.greenjab.fixedminecraft.mixin.transport;

import net.greenjab.fixedminecraft.items.ItemRegistry;
import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Prevents the horse wearing netherite armor from getting into angry pose when taking damage.
 */
@Mixin(AbstractHorseEntity.class)
public class ArmorAbstractHorseEntityMixin {
    @Shadow
    protected SimpleInventory items;

    @Inject(method = "updateAnger", at = @At("HEAD"), cancellable = true)
    private void rejectAngryWhenDrip(CallbackInfo ci) {
        ItemStack armor = items.getStack(1);
        System.out.println(Math.random());
        if (armor.getItem() == ItemRegistry.INSTANCE.getNETHERITE_HORSE_ARMOR())
            ci.cancel();
        if (armor.getItem() == Items.DIAMOND_HORSE_ARMOR)
            {if (Math.random()<0.9) ci.cancel();}
        if (armor.getItem() == Items.IRON_HORSE_ARMOR)
            {if (Math.random()<0.75) ci.cancel();}
        if (armor.getItem() == Items.GOLDEN_HORSE_ARMOR)
            {if (Math.random()<0.6) ci.cancel();}
        if (armor.getItem() == Items.LEATHER_HORSE_ARMOR)
            {if (Math.random()<0.45) ci.cancel();}
    }
}
