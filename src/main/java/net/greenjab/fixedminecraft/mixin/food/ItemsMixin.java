package net.greenjab.fixedminecraft.mixin.food;

import net.minecraft.item.FoodComponents;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.item.StewItem;
import net.minecraft.item.SuspiciousStewItem;
import net.minecraft.util.Rarity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(Items.class)
public class ItemsMixin {
    @Redirect(slice = @Slice(from = @At(value = "CONSTANT",args= {
            "stringValue=totem_of_undying"},ordinal = 0)),at = @At(
                    value = "NEW",target = "Lnet/minecraft/item/Item;*", ordinal = 0 ),method = "<clinit>")
    private static Item useableTotem(Item.Settings settings) {
        return new TotemItem((new Item.Settings()).maxCount(1).rarity(Rarity.UNCOMMON));
    }
    @Redirect(slice = @Slice(from = @At(value = "CONSTANT",args= {
            "stringValue=rabbit_stew"},ordinal = 0)),at = @At(
                    value = "NEW",target = "Lnet/minecraft/item/StewItem;*", ordinal = 0 ),method = "<clinit>")
    private static StewItem stackedRabbitstew(Item.Settings settings) {
        return new StewItem((new Item.Settings()).maxCount(16).food(FoodComponents.RABBIT_STEW));
    }
    @Redirect(slice = @Slice(from = @At(value = "CONSTANT",args= {
            "stringValue=mushroom_stew"},ordinal = 0)),at = @At(
                    value = "NEW",target = "Lnet/minecraft/item/StewItem;*", ordinal = 0 ),method = "<clinit>")
    private static StewItem stackedMushroomstew(Item.Settings settings) {
        return new StewItem((new Item.Settings()).maxCount(16).food(FoodComponents.MUSHROOM_STEW));
    }
    @Redirect(slice = @Slice(from = @At(value = "CONSTANT",args= {
            "stringValue=beetroot_soup"},ordinal = 0)),at = @At(
                    value = "NEW",target = "Lnet/minecraft/item/StewItem;*", ordinal = 0 ),method = "<clinit>")
    private static StewItem stackedBeetrootSoup(Item.Settings settings) {
        return new StewItem((new Item.Settings()).maxCount(16).food(FoodComponents.BEETROOT_SOUP));
    }
    @Redirect(slice = @Slice(from = @At(value = "CONSTANT",args= {
            "stringValue=suspicious_stew"},ordinal = 0)),at = @At(
                    value = "NEW",target = "Lnet/minecraft/item/SuspiciousStewItem;*", ordinal = 0 ),method = "<clinit>")
    private static SuspiciousStewItem stackedSuspiciousSoup(Item.Settings settings) {
        return new SuspiciousStewItem((new Item.Settings()).maxCount(16).food(FoodComponents.SUSPICIOUS_STEW));
    }
}
