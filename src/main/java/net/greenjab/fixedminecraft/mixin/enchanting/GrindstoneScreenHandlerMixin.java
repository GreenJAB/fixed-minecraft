package net.greenjab.fixedminecraft.mixin.enchanting;

import com.llamalad7.mixinextras.sugar.Local;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.greenjab.fixedminecraft.enchanting.FixedMinecraftEnchantmentHelper;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.CraftingResultInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.EnchantmentTags;
import net.minecraft.screen.*;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldEvents;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;


@Mixin(net.minecraft.screen.GrindstoneScreenHandler.class)
public abstract class GrindstoneScreenHandlerMixin extends ScreenHandler {

    @Shadow
    protected abstract ItemStack getOutputStack(ItemStack firstInput, ItemStack secondInput);

    public GrindstoneScreenHandlerMixin(
            @Nullable ScreenHandlerType<?> type, int syncId) {
        super(type, syncId);
    }

    @Redirect(method = "updateResult", at = @At(value = "INVOKE", target = "Lnet/minecraft/screen/GrindstoneScreenHandler;getOutputStack(Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ItemStack;)Lnet/minecraft/item/ItemStack;"))
    private ItemStack damageGrindstonedItem(GrindstoneScreenHandler instance, ItemStack i1, ItemStack i2) {
        if (i1.isEmpty()||i2.isEmpty()) {
            ItemStack original = getOutputStack(i1, i2);
            if (original.isOf(Items.BOOK) || original.isOf(Items.ENCHANTED_BOOK)) return original;
            boolean bl4 = !i1.isEmpty();
            if (bl4) {
                int max = i1.getMaxDamage();
                if (i1.getDamage() + max/4> max) {
                    return ItemStack.EMPTY;
                }
                ItemStack output = i1.copy();
                output.setDamage(i1.getDamage() + max/4);
                return getOutputStack(output, i2);
            } else {
                int max = i2.getMaxDamage();
                if (i2.getDamage() + max/4> max) {
                    return ItemStack.EMPTY;
                }
                ItemStack output = i2.copy();
                output.setDamage(i2.getDamage() + max/4);
                return getOutputStack(i1, output);
            }
        } else {
            return getOutputStack(i1, i2);
        }
    }

}
