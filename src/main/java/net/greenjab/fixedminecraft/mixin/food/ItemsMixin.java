package net.greenjab.fixedminecraft.mixin.food;


import net.greenjab.fixedminecraft.registry.item.BrickItem;
import net.greenjab.fixedminecraft.registry.item.GlisteringMelonSliceItem;
import net.greenjab.fixedminecraft.registry.item.PhantomMembraneItem;
import net.greenjab.fixedminecraft.registry.item.TotemItem;
import net.minecraft.item.FoodComponents;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.item.PotionItem;
import net.minecraft.item.SaddleItem;
import net.minecraft.item.StewItem;
import net.minecraft.item.SuspiciousStewItem;
import net.minecraft.util.Rarity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(Items.class)
public class ItemsMixin {
    @Redirect(method = "<clinit>",slice = @Slice(from = @At(value = "CONSTANT",args= {
            "stringValue=totem_of_undying"},ordinal = 0)),at = @At(
                    value = "NEW",target = "(Lnet/minecraft/item/Item$Settings;)Lnet/minecraft/item/Item;", ordinal = 0 ))
    private static Item useableTotem(Item.Settings settings) {
        return new TotemItem((new Item.Settings()).maxCount(1).rarity(Rarity.UNCOMMON));
    }
    @Redirect(method = "<clinit>", slice = @Slice(from = @At(value = "CONSTANT",args= {
            "stringValue=phantom_membrane"},ordinal = 0)),at = @At(
            value = "NEW",target = "(Lnet/minecraft/item/Item$Settings;)Lnet/minecraft/item/Item;", ordinal = 0 ))
    private static Item edibleMembrane(Item.Settings settings) {
        return new PhantomMembraneItem((new Item.Settings()).maxCount(64).food(FoodComponents.CHORUS_FRUIT));
    }

    @Redirect(method = "<clinit>", slice = @Slice(from = @At(value = "CONSTANT",args= {
            "stringValue=glistering_melon_slice"},ordinal = 0)),at = @At(
            value = "NEW",target = "(Lnet/minecraft/item/Item$Settings;)Lnet/minecraft/item/Item;", ordinal = 0 ))
    private static Item edibleGoldMelon(Item.Settings settings) {
        return new GlisteringMelonSliceItem((new Item.Settings()).maxCount(64).food(FoodComponents.GLOW_BERRIES));
    }

    @Redirect(method = "<clinit>",slice = @Slice(from = @At(value = "CONSTANT",args= {
            "stringValue=rabbit_stew"},ordinal = 0)),at = @At(
                    value = "NEW",target = "(Lnet/minecraft/item/Item$Settings;)Lnet/minecraft/item/StewItem;", ordinal = 0 ))
    private static StewItem stackedRabbitstew(Item.Settings settings) {
        return new StewItem((new Item.Settings()).maxCount(16).food(FoodComponents.RABBIT_STEW));
    }
    @Redirect(method = "<clinit>",slice = @Slice(from = @At(value = "CONSTANT",args= {
            "stringValue=mushroom_stew"},ordinal = 0)),at = @At(
                    value = "NEW",target = "(Lnet/minecraft/item/Item$Settings;)Lnet/minecraft/item/StewItem;", ordinal = 0 ))
    private static StewItem stackedMushroomstew(Item.Settings settings) {
        return new StewItem((new Item.Settings()).maxCount(16).food(FoodComponents.MUSHROOM_STEW));
    }
    @Redirect(method = "<clinit>",slice = @Slice(from = @At(value = "CONSTANT",args= {
            "stringValue=beetroot_soup"},ordinal = 0)),at = @At(
                    value = "NEW",target = "(Lnet/minecraft/item/Item$Settings;)Lnet/minecraft/item/StewItem;", ordinal = 0 ))
    private static StewItem stackedBeetrootSoup(Item.Settings settings) {
        return new StewItem((new Item.Settings()).maxCount(16).food(FoodComponents.BEETROOT_SOUP));
    }
    @Redirect(method = "<clinit>",slice = @Slice(from = @At(value = "CONSTANT",args= {
            "stringValue=suspicious_stew"},ordinal = 0)),at = @At(
            value = "NEW",target = "(Lnet/minecraft/item/Item$Settings;)Lnet/minecraft/item/SuspiciousStewItem;", ordinal = 0 ))
    private static SuspiciousStewItem stackedSuspiciousSoup(Item.Settings settings) {
        return new SuspiciousStewItem((new Item.Settings()).maxCount(16).food(FoodComponents.SUSPICIOUS_STEW));
    }

    @Redirect(method = "<clinit>",slice = @Slice(from = @At(value = "CONSTANT",args= {
            "stringValue=potion"},ordinal = 0)),at = @At(
            value = "NEW",target = "(Lnet/minecraft/item/Item$Settings;)Lnet/minecraft/item/PotionItem;", ordinal = 0 ))
    private static PotionItem stackedPotions(Item.Settings settings) {
        return new PotionItem((new Item.Settings()).maxCount(16));
    }

    @Redirect(method = "<clinit>",slice = @Slice(from = @At(value = "CONSTANT",args= {
            "stringValue=brick"},ordinal = 0)),at = @At(
            value = "NEW",target = "(Lnet/minecraft/item/Item$Settings;)Lnet/minecraft/item/Item;", ordinal = 0 ))
    private static Item throwableBrick(Item.Settings settings) {
        return new BrickItem((new Item.Settings()).maxCount(16));
    }

    @Redirect(method = "<clinit>",slice = @Slice(from = @At(value = "CONSTANT",args= {
            "stringValue=nether_brick"},ordinal = 0)),at = @At(
            value = "NEW",target = "(Lnet/minecraft/item/Item$Settings;)Lnet/minecraft/item/Item;", ordinal = 0 ))
    private static Item throwableNetherBrick(Item.Settings settings) {
        return new BrickItem((new Item.Settings()).maxCount(16));
    }

    @Redirect(method = "<clinit>",slice = @Slice(from = @At(value = "CONSTANT",args= {
            "stringValue=saddle"},ordinal = 0)),at = @At(
            value = "NEW",target = "(Lnet/minecraft/item/Item$Settings;)Lnet/minecraft/item/SaddleItem;"))
    private static SaddleItem stackedSaddles(Item.Settings settings) {
        return new SaddleItem((new Item.Settings()).maxCount(16));
    }

    @ModifyArg(method = "<clinit>", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/HoeItem;<init>(Lnet/minecraft/item/ToolMaterial;IFLnet/minecraft/item/Item$Settings;)V", ordinal = 4), index = 2)
    private static float hoeInditcatorDiamond(float attackSpeed) {
        return -0.5f;
    }
    @ModifyArg(method = "<clinit>", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/HoeItem;<init>(Lnet/minecraft/item/ToolMaterial;IFLnet/minecraft/item/Item$Settings;)V", ordinal = 5), index = 2)
    private static float hoeInditcatorNetherite(float attackSpeed) {
        return -0.5f;
    }

}
