package net.greenjab.fixedminecraft.mixin.enchanting;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.MergedComponentMap;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.item.AnimalArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {

    @Shadow
    public abstract boolean hasEnchantments();

    @Shadow
    @Final
    MergedComponentMap components;

    @Inject(method = "inventoryTick", at = @At("HEAD"))
    private void addGreenGlintUpdate(World world, Entity entity, int slot, boolean selected, CallbackInfo ci) {
        if (selected) {
            if (world.getTime()%20==0) {
                if (this.hasEnchantments()) {
                    ItemStack stack = (ItemStack) (Object) this;
                    ItemEnchantmentsComponent itemEnchantmentsComponent = stack.getOrDefault(DataComponentTypes.ENCHANTMENTS, ItemEnchantmentsComponent.DEFAULT);
                    for (RegistryEntry<Enchantment> enchantment : stack.getEnchantments().getEnchantments()) {
                        if (itemEnchantmentsComponent.getLevel(enchantment) > enchantment.value().getMaxLevel()) {
                            this.components.set(DataComponentTypes.REPAIR_COST, 1);
                            break;
                        }
                    }
                }
            }
        }
    }
}
