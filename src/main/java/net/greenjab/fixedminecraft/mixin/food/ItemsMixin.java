package net.greenjab.fixedminecraft.mixin.food;


import net.greenjab.fixedminecraft.registry.other.BaitComponent;
import net.greenjab.fixedminecraft.registry.registries.ItemRegistry;
import net.greenjab.fixedminecraft.registry.item.BrickItem;
import net.greenjab.fixedminecraft.registry.item.GlisteringMelonSliceItem;
import net.greenjab.fixedminecraft.registry.item.PhantomMembraneItem;
import net.greenjab.fixedminecraft.registry.item.TotemItem;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.component.type.FoodComponents;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.component.type.ChargedProjectilesComponent;
import net.minecraft.component.type.ConsumableComponents;
import net.minecraft.component.type.DeathProtectionComponent;
import net.minecraft.component.type.EquippableComponent;
import net.minecraft.component.type.FireworksComponent;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.component.type.FoodComponents;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.component.type.SuspiciousStewEffectsComponent;
import net.minecraft.item.BlockItem;
import net.minecraft.item.BowItem;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.FireworkRocketItem;
import net.minecraft.item.FishingRodItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.item.LingeringPotionItem;
import net.minecraft.item.PotionItem;
import net.minecraft.item.SplashPotionItem;
import net.minecraft.item.SaddleItem;
import net.minecraft.util.Rarity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;

import java.util.List;
import java.util.function.Function;
import static net.minecraft.item.Items.register;

@Mixin(Items.class)
public class ItemsMixin {

    @Redirect(method = "<clinit>",slice = @Slice(from = @At(value = "CONSTANT",args= {
            "stringValue=brick"},ordinal = 0)),at = @At(
            value = "NEW",target = "(Lnet/minecraft/item/Item$Settings;)Lnet/minecraft/item/Item;", ordinal = 0 ))
    private static Item throwableBrick(Item.Settings settings) {
        return new BrickItem((new Item.Settings()));
    }

    @Redirect(method = "<clinit>",slice = @Slice(from = @At(value = "CONSTANT",args= {
            "stringValue=nether_brick"},ordinal = 0)),at = @At(
            value = "NEW",target = "(Lnet/minecraft/item/Item$Settings;)Lnet/minecraft/item/Item;", ordinal = 0 ))
    private static Item throwableNetherBrick(Item.Settings settings) {
        return new BrickItem((new Item.Settings()));
    }

    @Redirect(method = "<clinit>",slice = @Slice(from = @At(value = "CONSTANT",args= {
            "stringValue=totem_of_undying"},ordinal = 0)),at = @At(
            value = "NEW",target = "(Lnet/minecraft/item/Item$Settings;)Lnet/minecraft/item/Item;", ordinal = 0 ))
    private static Item useableTotem(Item.Settings settings) {
        return new TotemItem((new Item.Settings().maxCount(1).rarity(Rarity.UNCOMMON)));
    }

    @Redirect(method = "<clinit>",slice = @Slice(from = @At(value = "CONSTANT",args= {
            "stringValue=phantom_membrane"},ordinal = 0)),at = @At(
            value = "NEW",target = "(Lnet/minecraft/item/Item$Settings;)Lnet/minecraft/item/Item;", ordinal = 0 ))
    private static Item edibleMembrane(Item.Settings settings) {
        return new PhantomMembraneItem(new Item.Settings().food(FoodComponents.CHORUS_FRUIT));
    }

    @Redirect(method = "<clinit>",slice = @Slice(from = @At(value = "CONSTANT",args= {
            "stringValue=glistering_melon_slice"},ordinal = 0)),at = @At(
            value = "NEW",target = "(Lnet/minecraft/item/Item$Settings;)Lnet/minecraft/item/Item;", ordinal = 0 ))
    private static Item edibleGoldMelon(Item.Settings settings) {
        return new GlisteringMelonSliceItem(new Item.Settings().food(new FoodComponent.Builder().nutrition(4).saturationModifier(0.8F).alwaysEdible().build()));
    }

    @Redirect(method = "<clinit>",slice = @Slice(from = @At(value = "CONSTANT",args= {
            "stringValue=rabbit_stew"},ordinal = 0)),at = @At(
            value = "NEW",target = "(Lnet/minecraft/item/Item$Settings;)Lnet/minecraft/item/Item;", ordinal = 0 ))
    private static Item stackedRabbitstew(Item.Settings settings) {
        return new Item(new Item.Settings().maxCount(16).food(FoodComponents.RABBIT_STEW));
    }

    @Redirect(method = "<clinit>",slice = @Slice(from = @At(value = "CONSTANT",args= {
            "stringValue=beetroot_soup"},ordinal = 0)),at = @At(
            value = "NEW",target = "(Lnet/minecraft/item/Item$Settings;)Lnet/minecraft/item/Item;", ordinal = 0 ))
    private static Item stackedBeetrootSoup(Item.Settings settings) {
        return new Item(new Item.Settings().maxCount(16).food(FoodComponents.BEETROOT_SOUP));
    }

    @Redirect(method = "<clinit>",slice = @Slice(from = @At(value = "CONSTANT",args= {
            "stringValue=mushroom_stew"},ordinal = 0)),at = @At(
            value = "NEW",target = "(Lnet/minecraft/item/Item$Settings;)Lnet/minecraft/item/Item;", ordinal = 0 ))
    private static Item stackedMushroomstew(Item.Settings settings) {
        return new Item(new Item.Settings().maxCount(16).food(FoodComponents.MUSHROOM_STEW));
    }

    @Redirect(method = "<clinit>",slice = @Slice(from = @At(value = "CONSTANT",args= {
            "stringValue=suspicious_stew"},ordinal = 0)),at = @At(
            value = "NEW",target = "(Lnet/minecraft/item/Item$Settings;)Lnet/minecraft/item/Item;", ordinal = 0 ))
    private static Item stackedSuspiciousSoup(Item.Settings settings) {
        return new Item(new Item.Settings().maxCount(16).food(FoodComponents.SUSPICIOUS_STEW));
    }

    @Redirect(method = "<clinit>",slice = @Slice(from = @At(value = "CONSTANT",args= {
            "stringValue=potion"},ordinal = 0)),at = @At(
            value = "NEW",target = "(Lnet/minecraft/item/Item$Settings;)Lnet/minecraft/item/PotionItem;", ordinal = 0 ))
    private static PotionItem stackedPotions(Item.Settings settings) {
        return new PotionItem(new Item.Settings().maxCount(16).component(DataComponentTypes.POTION_CONTENTS, PotionContentsComponent.DEFAULT));
    }

    @Redirect(method = "<clinit>",slice = @Slice(from = @At(value = "CONSTANT",args= {
            "stringValue=splash_potion"},ordinal = 0)),at = @At(
            value = "NEW",target = "(Lnet/minecraft/item/Item$Settings;)Lnet/minecraft/item/SplashPotionItem;" , ordinal = 0))
    private static SplashPotionItem stackedSplashPotions(Item.Settings settings) {
        return new SplashPotionItem(new Item.Settings().maxCount(16).component(DataComponentTypes.POTION_CONTENTS, PotionContentsComponent.DEFAULT));
    }

    @Redirect(method = "<clinit>",slice = @Slice(from = @At(value = "CONSTANT",args= {
            "stringValue=lingering_potion"},ordinal = 0)),at = @At(
            value = "NEW",target = "(Lnet/minecraft/item/Item$Settings;)Lnet/minecraft/item/LingeringPotionItem;", ordinal = 0 ))
    private static LingeringPotionItem stackedLingeringPotions(Item.Settings settings) {
        return new LingeringPotionItem(new Item.Settings().maxCount(16).component(DataComponentTypes.POTION_CONTENTS, PotionContentsComponent.DEFAULT));
    }

    @Redirect(method = "<clinit>",slice = @Slice(from = @At(value = "CONSTANT",args= {
            "stringValue=saddle"},ordinal = 0)),at = @At(
            value = "NEW",target = "(Lnet/minecraft/item/Item$Settings;)Lnet/minecraft/item/SaddleItem;", ordinal = 0 ))
    private static SaddleItem stackedSaddles(Item.Settings settings) {
        return new SaddleItem(new Item.Settings().maxCount(16));
    }

    @Redirect(method = "<clinit>",slice = @Slice(from = @At(value = "CONSTANT",args= {
            "stringValue=spider_eye"},ordinal = 0)),at = @At(
            value = "NEW",target = "(Lnet/minecraft/item/Item$Settings;)Lnet/minecraft/item/Item;", ordinal = 0 ))
    private static Item spiderEyeBait(Item.Settings settings) {
        return new Item(new Item.Settings().food(FoodComponents.SPIDER_EYE).component(ItemRegistry.BAIT_POWER, new BaitComponent(1)));
    }

    @Redirect(method = "<clinit>",slice = @Slice(from = @At(value = "CONSTANT",args= {
            "stringValue=fermented_spider_eye"},ordinal = 0)),at = @At(
            value = "NEW",target = "(Lnet/minecraft/item/Item$Settings;)Lnet/minecraft/item/Item;", ordinal = 0 ))
    private static Item fermentedSpiderEyeBait(Item.Settings settings) {
        return new Item(new Item.Settings().component(ItemRegistry.BAIT_POWER, new BaitComponent(2)));
    }

    /*@Redirect(method="<clinit>", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/Items;register(Ljava/lang/String;Lnet/minecraft/item/Item;)Lnet/minecraft/item/Item;", ordinal = 0), slice = @Slice( from = @At(value = "FIELD",
                                                                                                                                                                                                  target = "Lnet/minecraft/item/Items;TADPOLE_BUCKET:Lnet/minecraft/item/Item;")))
    private static Item throwableBrick(String id, Item item) {
        return register("brick", new BrickItem(new Item.Settings()));
    }

    @Redirect(method="<clinit>", at = @At( value = "INVOKE", target = "Lnet/minecraft/item/Items;register(Ljava/lang/String;Lnet/minecraft/item/Item;)Lnet/minecraft/item/Item;", ordinal = 0 ), slice = @Slice(from = @At( value = "FIELD",
                                  target = "Lnet/minecraft/item/Items;ENCHANTED_BOOK:Lnet/minecraft/item/Item;")))
    private static Item throwableNetherBrick(String id, Item item) {
        return register("nether_brick", new BrickItem(new Item.Settings()));
    }


    @Redirect(method="<clinit>", at = @At( value = "INVOKE", target = "Lnet/minecraft/item/Items;register(Ljava/lang/String;Lnet/minecraft/item/Item;)Lnet/minecraft/item/Item;", ordinal = 0 ), slice = @Slice(from = @At( value = "FIELD",
                                   target = "Lnet/minecraft/item/Items;SHIELD:Lnet/minecraft/item/Item;")))
    private static Item useableTotem(String id, Item item) {
        return register("totem_of_undying", new TotemItem(new Item.Settings().maxCount(1).rarity(Rarity.UNCOMMON)));
    }

    @Redirect(method="<clinit>", at = @At( value = "INVOKE", target = "Lnet/minecraft/item/Items;register(Ljava/lang/String;Lnet/minecraft/item/Item;)Lnet/minecraft/item/Item;", ordinal = 0 ), slice = @Slice(from = @At( value = "FIELD",
                                   target = "Lnet/minecraft/item/Items;TRIDENT:Lnet/minecraft/item/Item;")))
    private static Item edibleMembrane(String id, Item item) {
        return register("phantom_membrane", new PhantomMembraneItem(new Item.Settings().food(FoodComponents.CHORUS_FRUIT)));
    }

    @Redirect(method="<clinit>", at = @At( value = "INVOKE", target = "Lnet/minecraft/item/Items;register(Ljava/lang/String;Lnet/minecraft/item/Item;)Lnet/minecraft/item/Item;", ordinal = 0 ), slice = @Slice(from = @At( value = "FIELD",
                                                                                                                                                                                                   target = "Lnet/minecraft/item/Items;ENDER_EYE:Lnet/minecraft/item/Item;")))
    private static Item edibleGoldMelon(String id, Item item) {
        return register("glistering_melon_slice", new GlisteringMelonSliceItem(new Item.Settings().food(new FoodComponent.Builder().nutrition(4).saturationModifier(0.8F).alwaysEdible().build())));
    }

    /*@Redirect(method="<clinit>", at = @At( value = "INVOKE", target = "Lnet/minecraft/item/Items;register(Ljava/lang/String;Lnet/minecraft/item/Item;)Lnet/minecraft/item/Item;", ordinal = 0 ), slice = @Slice(from = @At( value = "FIELD",
                                                         target = "Lnet/minecraft/item/Items;SWEET_BERRIES:Lnet/minecraft/item/Item;")))
    private static Item glowingGlowBerries(String id, Item item) {
        return register("glow_berries", createBlockItemWithUniqueName(Blocks.CAVE_VINES), new Item.Settings().food(FoodComponents.HONEY_BOTTLE, ItemRegistry.GLOW_BERRIES_EFFECT));
    }*/

    /*@Redirect(method="<clinit>", at = @At( value = "INVOKE", target = "Lnet/minecraft/item/Items;register(Ljava/lang/String;Lnet/minecraft/item/Item;)Lnet/minecraft/item/Item;", ordinal = 0 ), slice = @Slice(from = @At( value = "FIELD",
                                   target = "Lnet/minecraft/item/Items;COOKED_RABBIT:Lnet/minecraft/item/Item;")))
    private static Item stackedRabbitstew(String id, Item item) {
        return register("rabbit_stew", new Item(new Item.Settings().maxCount(16).food(FoodComponents.RABBIT_STEW)));
    }

    @Redirect(method="<clinit>", at = @At( value = "INVOKE", target = "Lnet/minecraft/item/Items;register(Ljava/lang/String;Lnet/minecraft/item/Item;)Lnet/minecraft/item/Item;", ordinal = 0 ), slice = @Slice(from = @At( value = "FIELD",
                                                                                                                                                                                                                                     target = "Lnet/minecraft/item/Items;BEETROOT_SEEDS:Lnet/minecraft/item/Item;")))
    private static Item stackedBeetrootSoup(String id, Item item) {
        return register("beetroot_soup", new Item(new Item.Settings().maxCount(16).food(FoodComponents.BEETROOT_SOUP)));
    }

    @Redirect(method="<clinit>", at = @At( value = "INVOKE", target = "Lnet/minecraft/item/Items;register(Ljava/lang/String;Lnet/minecraft/item/Item;)Lnet/minecraft/item/Item;", ordinal = 0 ), slice = @Slice(from = @At( value = "FIELD",
                                   target = "Lnet/minecraft/item/Items;STICK:Lnet/minecraft/item/Item;")))
    private static Item stackedMushroomstew(String id, Item item) {
        return register("mushroom_stew", new Item(new Item.Settings().maxCount(16).food(FoodComponents.MUSHROOM_STEW)));
    }

    @Redirect(method="<clinit>", at = @At( value = "INVOKE", target = "Lnet/minecraft/item/Items;register(Ljava/lang/String;Lnet/minecraft/item/Item;)Lnet/minecraft/item/Item;", ordinal = 0 ), slice = @Slice(from = @At( value = "FIELD",
                                   target = "Lnet/minecraft/item/Items;CROSSBOW:Lnet/minecraft/item/Item;")))
    private static Item stackedSuspiciousSoup(String id, Item item) {
        return register("suspicious_stew", new Item(new Item.Settings().maxCount(16).food(FoodComponents.SUSPICIOUS_STEW).component(DataComponentTypes.SUSPICIOUS_STEW_EFFECTS, SuspiciousStewEffectsComponent.DEFAULT)));
    }

    @Redirect(method="<clinit>", at = @At( value = "INVOKE", target = "Lnet/minecraft/item/Items;register(Ljava/lang/String;Lnet/minecraft/item/Item;)Lnet/minecraft/item/Item;", ordinal = 0 ), slice = @Slice(from = @At( value = "FIELD",
                                   target = "Lnet/minecraft/item/Items;NETHER_WART:Lnet/minecraft/item/Item;")))
    private static Item stackedPotions(String id, Item item) {
        return register("potion", new PotionItem(new Item.Settings().maxCount(16).component(DataComponentTypes.POTION_CONTENTS, PotionContentsComponent.DEFAULT)));
    }

    @Redirect(method="<clinit>", at = @At( value = "INVOKE", target = "Lnet/minecraft/item/Items;register(Ljava/lang/String;Lnet/minecraft/item/Item;)Lnet/minecraft/item/Item;", ordinal = 0 ), slice = @Slice(from = @At( value = "FIELD",
                                                           target = "Lnet/minecraft/item/Items;DRAGON_BREATH:Lnet/minecraft/item/Item;")))
    private static Item stackedSplashPotions(String id, Item item) {
        return register("splash_potion", new SplashPotionItem(new Item.Settings().maxCount(16).component(DataComponentTypes.POTION_CONTENTS, PotionContentsComponent.DEFAULT)));
    }

    @Redirect(method="<clinit>", at = @At( value = "INVOKE", target = "Lnet/minecraft/item/Items;register(Ljava/lang/String;Lnet/minecraft/item/Item;)Lnet/minecraft/item/Item;", ordinal = 0 ), slice = @Slice(from = @At( value = "FIELD",
                                                         target = "Lnet/minecraft/item/Items;TIPPED_ARROW:Lnet/minecraft/item/Item;")))
    private static Item stackedLingeringPotions(String id, Item item) {
        return register("lingering_potion",
                new LingeringPotionItem
                (new Item.Settings()
                        .maxCount(16)
                        .component(DataComponentTypes.POTION_CONTENTS, PotionContentsComponent.DEFAULT)));
    }

    @Redirect(method="<clinit>", at = @At( value = "INVOKE", target = "Lnet/minecraft/item/Items;register(Ljava/lang/String;Lnet/minecraft/item/Item;)Lnet/minecraft/item/Item;", ordinal = 0 ), slice = @Slice(from = @At( value = "FIELD",
                                   target = "Lnet/minecraft/item/Items;ACTIVATOR_RAIL:Lnet/minecraft/item/Item;")))
    private static Item stackedSaddles(String id, Item item) {
        return register("saddle", new SaddleItem(new Item.Settings().maxCount(16)));
    }

    /*@Redirect(method="<clinit>", at = @At( value = "INVOKE", target = "Lnet/minecraft/item/Items;register(Ljava/lang/String;Lnet/minecraft/item/Item;)Lnet/minecraft/item/Item;", ordinal = 0 ), slice = @Slice(from = @At( value = "FIELD",
                                   target = "Lnet/minecraft/item/Items;DISC_FRAGMENT_5:Lnet/minecraft/item/Item;")))
    private static Item repairableTrident(String id, Item item) {
        return register("trident", new TridentItem(new Item.Settings().rarity(Rarity.EPIC).maxDamage(250).attributeModifiers(TridentItem.createAttributeModifiers()).component(DataComponentTypes.TOOL, TridentItem.createToolComponent()).enchantable(1).repairable(Items.PRISMARINE_SHARD)));
    }

    //As string is initilized after bow, need to pass itemtag os just string rather than string itself
    @Redirect(method="<clinit>", at = @At( value = "INVOKE", target = "Lnet/minecraft/item/Items;register(Ljava/lang/String;Lnet/minecraft/item/Item;)Lnet/minecraft/item/Item;", ordinal = 0 ), slice = @Slice(from = @At( value = "FIELD",
                                    target = "Lnet/minecraft/item/Items;APPLE:Lnet/minecraft/item/Item;")))
    private static Item repairableBow(String id, Item item) {
        return register("bow", new BowItem(new Item.Settings().maxDamage(384).enchantable(1).repairable(ModTags.STRINGTAG)));
    }

    @Redirect(method="<clinit>", at = @At( value = "INVOKE", target = "Lnet/minecraft/item/Items;register(Ljava/lang/String;Lnet/minecraft/item/Item;)Lnet/minecraft/item/Item;", ordinal = 0 ), slice = @Slice(from = @At( value = "FIELD",
                                   target = "Lnet/minecraft/item/Items;HEART_OF_THE_SEA:Lnet/minecraft/item/Item;")))
    private static Item repairableCrossBow(String id, Item item) {
        return register("crossbow",
                new CrossbowItem
                (new Item.Settings().maxCount(1).maxDamage(465).component(DataComponentTypes.CHARGED_PROJECTILES, ChargedProjectilesComponent.DEFAULT).enchantable(1).repairable(Items.STRING)));
    }

    @Redirect(method="<clinit>", at = @At( value = "INVOKE", target = "Lnet/minecraft/item/Items;register(Ljava/lang/String;Lnet/minecraft/item/Item;)Lnet/minecraft/item/Item;", ordinal = 0 ), slice = @Slice(from = @At( value = "FIELD",
                                 target = "Lnet/minecraft/item/Items;BLACK_BUNDLE:Lnet/minecraft/item/Item;")))
    private static Item repairableFishingRod(String id, Item item) {
        return register("fishing_rod", new FishingRodItem(new Item.Settings().maxDamage(64).enchantable(1).repairable(Items.STRING)));
    }*/

   /* @Redirect(method="<clinit>", at = @At( value = "INVOKE", target = "Lnet/minecraft/item/Items;register(Ljava/lang/String;Lnet/minecraft/item/Item;)Lnet/minecraft/item/Item;", ordinal = 0), slice = @Slice(from = @At( value = "FIELD",
                                  target = "Lnet/minecraft/item/Items;GLASS_BOTTLE:Lnet/minecraft/item/Item;")))
    private static Item spiderEyeBait(String id, Item item) {
        return register("spider_eye", new Item(new Item.Settings().food(FoodComponents.SPIDER_EYE).component(ItemRegistry.BAIT_POWER, new BaitComponent(1))));
    }

    @Redirect(method="<clinit>", at = @At( value = "INVOKE", target = "Lnet/minecraft/item/Items;register(Ljava/lang/String;Lnet/minecraft/item/Item;)Lnet/minecraft/item/Item;", ordinal = 0), slice = @Slice(from = @At( value = "FIELD",
                                  target = "Lnet/minecraft/item/Items;SPIDER_EYE:Lnet/minecraft/item/Item;")))
    private static Item fermentedSpiderEyeBait(String id, Item item) {
        return register("fermented_spider_eye", new Item(new Item.Settings().component(ItemRegistry.BAIT_POWER, new BaitComponent(2))));
    }*/


    @Redirect(method="<clinit>", at = @At( value = "INVOKE", target = "Lnet/minecraft/item/Items;register(Ljava/lang/String;Ljava/util/function/Function;Lnet/minecraft/item/Item$Settings;)Lnet/minecraft/item/Item;", ordinal = 0), slice = @Slice(from = @At( value = "FIELD",
                     target = "Lnet/minecraft/item/Items;PUMPKIN_PIE:Lnet/minecraft/item/Item;")))
    private static Item fireWorkCooldown(String id, Function<Item.Settings, Item> factory, Item.Settings settings) {
        return register(
                "firework_rocket", FireworkRocketItem::new, new Item.Settings().component(DataComponentTypes.FIREWORKS, new FireworksComponent(1, List.of())).useCooldown(5)
        );
    }

    @Unique
    private static Function<Item.Settings, Item> createBlockItemWithUniqueName(Block block) {
        return settings -> new BlockItem(block, settings.useItemPrefixedTranslationKey());
    }
}
