package net.greenjab.fixedminecraft.mixin.villager;

import com.llamalad7.mixinextras.sugar.Local;
import net.greenjab.fixedminecraft.mobs.ArmorTrimmer;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.tag.EnchantmentTags;
import net.minecraft.util.math.random.Random;
import net.minecraft.village.TradeOffers;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.Optional;

@Mixin(TradeOffers.SellDyedArmorFactory.class)
public class SellDyedArmorFactoryMixin {

    @Shadow
    @Final
    private int experience;

    @ModifyArg(method = "create", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/village/TradeOffer;<init>(Lnet/minecraft/village/TradedItem;Lnet/minecraft/item/ItemStack;IIF)V"
    ), index = 1)
    private ItemStack enchantedLeatherGear(ItemStack sellItem, @Local(argsOnly = true) Entity entity, @Local(argsOnly = true) Random random){
        if (this.experience>5) {
            int i = 15;
            DynamicRegistryManager dynamicRegistryManager = entity.getWorld().getRegistryManager();
            Optional<RegistryEntryList.Named<Enchantment>> optional = dynamicRegistryManager.getOrThrow(RegistryKeys.ENCHANTMENT)
                    .getOptional(EnchantmentTags.ON_TRADED_EQUIPMENT);
            sellItem = EnchantmentHelper.enchant(random, sellItem, i, dynamicRegistryManager, optional);
            if (!sellItem.isOf(Items.LEATHER_HORSE_ARMOR)){
                ArmorTrimmer.trimAtChanceIfTrimable(sellItem, random, entity.getWorld().getRegistryManager(), true);
            }
        }
        return sellItem;
    }
}
