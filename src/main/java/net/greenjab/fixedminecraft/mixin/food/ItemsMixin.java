package net.greenjab.fixedminecraft.mixin.food;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.greenjab.fixedminecraft.registry.ModTags;
import net.greenjab.fixedminecraft.registry.other.BaitComponent;
import net.greenjab.fixedminecraft.registry.registries.ItemRegistry;
import net.greenjab.fixedminecraft.registry.registries.TrimMaterialsRegistry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.food.Foods;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.component.DamageResistant;
import net.minecraft.world.item.component.DeathProtection;
import net.minecraft.world.item.equipment.trim.TrimMaterials;
import net.greenjab.fixedminecraft.registry.item.NewBrickItem;
import net.greenjab.fixedminecraft.registry.item.NewGlisteringMelonSliceItem;
import net.greenjab.fixedminecraft.registry.item.NewPhantomMembraneItem;
import net.greenjab.fixedminecraft.registry.item.NewTotemItem;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;
import java.util.function.Function;

@Mixin(Items.class)
public abstract class ItemsMixin {

    @Shadow private static Item registerItem(ResourceKey<Item> id, Function<Item.Properties, Item> itemFactory, Item.Properties properties) {
        throw new UnsupportedOperationException("Implemented via mixin");
    }

    @Shadow private static Item registerItem(ResourceKey<Item> id, Item.Properties properties) {
        throw new UnsupportedOperationException("Implemented via mixin");
    }

    @WrapOperation(method="<clinit>", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/Items;registerItem(Lnet/minecraft/resources/ResourceKey;)Lnet/minecraft/world/item/Item;"), slice = @Slice(from =
    @At(value = "FIELD", target = "Lnet/minecraft/references/ItemIds;BRICK:Lnet/minecraft/resources/ResourceKey;", opcode = Opcodes.GETSTATIC), to =
    @At(value = "FIELD",target = "Lnet/minecraft/world/item/Items;BRICK:Lnet/minecraft/world/item/Item;", opcode = Opcodes.PUTSTATIC)))
    private static Item throwableBrick(ResourceKey<Item> id, Operation<Item> original) {
        return registerItem(id, NewBrickItem::new, new Item.Properties().useCooldown(1));}

    @WrapOperation(method="<clinit>", at = @At( value = "INVOKE", target = "Lnet/minecraft/world/item/Items;registerItem(Lnet/minecraft/resources/ResourceKey;)Lnet/minecraft/world/item/Item;", ordinal = 0 ), slice = @Slice(from =
    @At(value = "FIELD", target = "Lnet/minecraft/references/ItemIds;NETHER_BRICK:Lnet/minecraft/resources/ResourceKey;", opcode = Opcodes.GETSTATIC), to =
    @At(value = "FIELD",target = "Lnet/minecraft/world/item/Items;NETHER_BRICK:Lnet/minecraft/world/item/Item;", opcode = Opcodes.PUTSTATIC)))
    private static Item throwableNetherBrick(ResourceKey<Item> id, Operation<Item> original) {
        return registerItem(id, NewBrickItem::new, new Item.Properties().useCooldown(1));}

    @WrapOperation(method="<clinit>", at = @At( value = "INVOKE", target = "Lnet/minecraft/world/item/Items;registerItem(Lnet/minecraft/resources/ResourceKey;Lnet/minecraft/world/item/Item$Properties;)Lnet/minecraft/world/item/Item;", ordinal = 0), slice = @Slice(from =
    @At(value = "FIELD", target = "Lnet/minecraft/references/ItemIds;RESIN_BRICK:Lnet/minecraft/resources/ResourceKey;", opcode = Opcodes.GETSTATIC), to =
    @At(value = "FIELD",target = "Lnet/minecraft/world/item/Items;RESIN_BRICK:Lnet/minecraft/world/item/Item;", opcode = Opcodes.PUTSTATIC)))
    private static Item throwableResinBrick(ResourceKey<Item> id, Item.Properties properties, Operation<Item> original) {
        return registerItem(id, NewBrickItem::new, new Item.Properties().useCooldown(1).trimMaterial(TrimMaterials.RESIN));}

    @WrapOperation(method="<clinit>", at = @At( value = "INVOKE", target = "Lnet/minecraft/world/item/Items;registerItem(Lnet/minecraft/resources/ResourceKey;Lnet/minecraft/world/item/Item$Properties;)Lnet/minecraft/world/item/Item;", ordinal = 0 ), slice = @Slice(from =
    @At(value = "FIELD", target = "Lnet/minecraft/references/ItemIds;TOTEM_OF_UNDYING:Lnet/minecraft/resources/ResourceKey;", opcode = Opcodes.GETSTATIC), to =
    @At(value = "FIELD",target = "Lnet/minecraft/world/item/Items;TOTEM_OF_UNDYING:Lnet/minecraft/world/item/Item;", opcode = Opcodes.PUTSTATIC)))
    private static Item useableTotem(ResourceKey<Item> id, Item.Properties properties, Operation<Item> original) {
        return registerItem(id, NewTotemItem::new, new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON).component(DataComponents.DEATH_PROTECTION, DeathProtection.TOTEM_OF_UNDYING));}

    @WrapOperation(method="<clinit>", at = @At( value = "INVOKE", target = "Lnet/minecraft/world/item/Items;registerItem(Lnet/minecraft/resources/ResourceKey;)Lnet/minecraft/world/item/Item;", ordinal = 0 ), slice = @Slice(from =
    @At(value = "FIELD", target = "Lnet/minecraft/references/ItemIds;PHANTOM_MEMBRANE:Lnet/minecraft/resources/ResourceKey;", opcode = Opcodes.GETSTATIC), to =
    @At(value = "FIELD",target = "Lnet/minecraft/world/item/Items;PHANTOM_MEMBRANE:Lnet/minecraft/world/item/Item;", opcode = Opcodes.PUTSTATIC)))
    private static Item edibleMembrane(ResourceKey<Item> id, Operation<Item> original) {
        return registerItem(id, NewPhantomMembraneItem::new, new Item.Properties().stacksTo(64).food(Foods.CHORUS_FRUIT));}

    @WrapOperation(method="<clinit>", at = @At( value = "INVOKE", target = "Lnet/minecraft/world/item/Items;registerItem(Lnet/minecraft/resources/ResourceKey;)Lnet/minecraft/world/item/Item;", ordinal = 0 ), slice = @Slice(from =
    @At(value = "FIELD", target = "Lnet/minecraft/references/ItemIds;GLISTERING_MELON_SLICE:Lnet/minecraft/resources/ResourceKey;", opcode = Opcodes.GETSTATIC), to =
    @At(value = "FIELD",target = "Lnet/minecraft/world/item/Items;GLISTERING_MELON_SLICE:Lnet/minecraft/world/item/Item;", opcode = Opcodes.PUTSTATIC)))
    private static Item edibleGoldMelon(ResourceKey<Item> id, Operation<Item> original) {
        return registerItem(id, NewGlisteringMelonSliceItem::new, new Item.Properties().food(new FoodProperties.Builder().nutrition(4).saturationModifier(0.8F).build()));}


    @WrapOperation(method="<clinit>", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/Item$Properties;food(Lnet/minecraft/world/food/FoodProperties;)Lnet/minecraft/world/item/Item$Properties;", ordinal = 0 ), slice = @Slice(from =
    @At(value = "FIELD", target = "Lnet/minecraft/references/BlockItemIds;GLOW_BERRY_CROP:Lnet/minecraft/references/BlockItemId;", opcode = Opcodes.GETSTATIC), to =
    @At(value = "FIELD",target = "Lnet/minecraft/world/item/Items;GLOW_BERRIES:Lnet/minecraft/world/item/Item;", opcode = Opcodes.PUTSTATIC)))
    private static Item.Properties glowingGlowBerries(Item.Properties instance, FoodProperties foodProperties,Operation<Item.Properties> original) {
        return instance.food(Foods.GLOW_BERRIES, ItemRegistry.GLOW_BERRIES_EFFECT);}

    @ModifyArg(method="<clinit>", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/Item$Properties;stacksTo(I)Lnet/minecraft/world/item/Item$Properties;", ordinal = 0 ), slice = @Slice(from =
    @At(value = "FIELD", target = "Lnet/minecraft/references/ItemIds;RABBIT_STEW:Lnet/minecraft/resources/ResourceKey;", opcode = Opcodes.GETSTATIC), to =
    @At(value = "FIELD",target = "Lnet/minecraft/world/item/Items;RABBIT_STEW:Lnet/minecraft/world/item/Item;", opcode = Opcodes.PUTSTATIC)))
    private static int stackedRabbitStew(int max) {
        return 16;}

    @ModifyArg(method="<clinit>", at = @At( value = "INVOKE", target = "Lnet/minecraft/world/item/Item$Properties;stacksTo(I)Lnet/minecraft/world/item/Item$Properties;", ordinal = 0 ), slice = @Slice(from =
    @At(value = "FIELD", target = "Lnet/minecraft/references/ItemIds;BEETROOT_SOUP:Lnet/minecraft/resources/ResourceKey;", opcode = Opcodes.GETSTATIC), to =
    @At(value = "FIELD",target = "Lnet/minecraft/world/item/Items;BEETROOT_SOUP:Lnet/minecraft/world/item/Item;", opcode = Opcodes.PUTSTATIC)))
    private static int stackedBeetrootSoup(int max) {
        return 16;}

    @ModifyArg(method="<clinit>", at = @At( value = "INVOKE", target = "Lnet/minecraft/world/item/Item$Properties;stacksTo(I)Lnet/minecraft/world/item/Item$Properties;", ordinal = 0 ), slice = @Slice(from =
    @At(value = "FIELD", target = "Lnet/minecraft/references/ItemIds;MUSHROOM_STEW:Lnet/minecraft/resources/ResourceKey;", opcode = Opcodes.GETSTATIC), to =
    @At(value = "FIELD",target = "Lnet/minecraft/world/item/Items;MUSHROOM_STEW:Lnet/minecraft/world/item/Item;", opcode = Opcodes.PUTSTATIC)))
    private static int stackedMushroomStew(int max) {
        return 16;}

    @ModifyArg(method="<clinit>", at = @At( value = "INVOKE", target = "Lnet/minecraft/world/item/Item$Properties;stacksTo(I)Lnet/minecraft/world/item/Item$Properties;", ordinal = 0 ), slice = @Slice(from =
    @At(value = "FIELD", target = "Lnet/minecraft/references/ItemIds;SUSPICIOUS_STEW:Lnet/minecraft/resources/ResourceKey;", opcode = Opcodes.GETSTATIC), to =
    @At(value = "FIELD",target = "Lnet/minecraft/world/item/Items;SUSPICIOUS_STEW:Lnet/minecraft/world/item/Item;", opcode = Opcodes.PUTSTATIC)))
    private static int stackedSuspiciousSoup(int max) {
        return 16;}

    @WrapOperation(method="<clinit>", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/Item$Properties;stacksTo(I)Lnet/minecraft/world/item/Item$Properties;", ordinal = 0 ), slice = @Slice(from =
    @At(value = "FIELD", target = "Lnet/minecraft/references/ItemIds;POTION:Lnet/minecraft/resources/ResourceKey;", opcode = Opcodes.GETSTATIC), to =
    @At(value = "FIELD",target = "Lnet/minecraft/world/item/Items;POTION:Lnet/minecraft/world/item/Item;", opcode = Opcodes.PUTSTATIC)))
    private static Item.Properties stackedPotions(Item.Properties instance, int max, Operation<Item.Properties> original) {
        return original.call(instance, 16);}

    @WrapOperation(method="<clinit>", at = @At( value = "INVOKE", target = "Lnet/minecraft/world/item/Item$Properties;stacksTo(I)Lnet/minecraft/world/item/Item$Properties;", ordinal = 0 ), slice = @Slice(from =
    @At(value = "FIELD", target = "Lnet/minecraft/references/ItemIds;SPLASH_POTION:Lnet/minecraft/resources/ResourceKey;", opcode = Opcodes.GETSTATIC), to =
    @At(value = "FIELD",target = "Lnet/minecraft/world/item/Items;SPLASH_POTION:Lnet/minecraft/world/item/Item;", opcode = Opcodes.PUTSTATIC)))
    private static Item.Properties stackedSplashPotions(Item.Properties instance, int max, Operation<Item.Properties> original) {
        return original.call(instance, 16).useCooldown(3);}

    @WrapOperation(method="<clinit>", at = @At( value = "INVOKE", target = "Lnet/minecraft/world/item/Item$Properties;stacksTo(I)Lnet/minecraft/world/item/Item$Properties;", ordinal = 0 ), slice = @Slice(from =
    @At(value = "FIELD", target = "Lnet/minecraft/references/ItemIds;LINGERING_POTION:Lnet/minecraft/resources/ResourceKey;", opcode = Opcodes.GETSTATIC), to =
    @At(value = "FIELD",target = "Lnet/minecraft/world/item/Items;LINGERING_POTION:Lnet/minecraft/world/item/Item;", opcode = Opcodes.PUTSTATIC)))
    private static Item.Properties stackedLingeringPotions(Item.Properties instance, int max, Operation<Item.Properties> original) {
        return original.call(instance, 16).useCooldown(3);}

    @WrapOperation(method="<clinit>", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/Item$Properties;food(Lnet/minecraft/world/food/FoodProperties;)Lnet/minecraft/world/item/Item$Properties;", ordinal = 0 ), slice = @Slice(from =
    @At(value = "FIELD", target = "Lnet/minecraft/references/BlockItemIds;SWEET_BERRY_CROP:Lnet/minecraft/references/BlockItemId;", opcode = Opcodes.GETSTATIC), to =
    @At(value = "FIELD",target = "Lnet/minecraft/world/item/Items;SWEET_BERRIES:Lnet/minecraft/world/item/Item;", opcode = Opcodes.PUTSTATIC)))
    private static Item.Properties speedSweetBerries(Item.Properties instance, FoodProperties foodProperties, Operation<Item.Properties> original) {
        return instance.food(Foods.SWEET_BERRIES, ItemRegistry.SWEET_BERRIES_EFFECT);}

    @ModifyArg(method="<clinit>", at = @At( value = "INVOKE", target = "Lnet/minecraft/world/item/Item$Properties;stacksTo(I)Lnet/minecraft/world/item/Item$Properties;", ordinal = 0), slice = @Slice(from =
    @At(value = "FIELD", target = "Lnet/minecraft/references/ItemIds;SADDLE:Lnet/minecraft/resources/ResourceKey;", opcode = Opcodes.GETSTATIC), to =
    @At(value = "FIELD",target = "Lnet/minecraft/world/item/Items;SADDLE:Lnet/minecraft/world/item/Item;", opcode = Opcodes.PUTSTATIC)))
    private static int stackedSaddles(int max) {
        return 16;}

    @ModifyArg(method="<clinit>", at = @At( value = "INVOKE", target = "Lnet/minecraft/world/item/Items;registerItem(Lnet/minecraft/resources/ResourceKey;Ljava/util/function/Function;Lnet/minecraft/world/item/Item$Properties;)Lnet/minecraft/world/item/Item;", ordinal = 0 ), slice = @Slice(from =
    @At(value = "FIELD", target = "Lnet/minecraft/references/ItemIds;TRIDENT:Lnet/minecraft/resources/ResourceKey;", opcode = Opcodes.GETSTATIC), to =
    @At(value = "FIELD",target = "Lnet/minecraft/world/item/Items;TRIDENT:Lnet/minecraft/world/item/Item;", opcode = Opcodes.PUTSTATIC)), index = 2)
    private static Item.Properties repairableTrident(Item.Properties properties) {
        return properties.repairable(Items.PRISMARINE_SHARD);}

    //As string is initilized after bow, need to pass itemtag of just string rather than string itself
    @ModifyArg(method="<clinit>", at = @At( value = "INVOKE", target = "Lnet/minecraft/world/item/Items;registerItem(Lnet/minecraft/resources/ResourceKey;Ljava/util/function/Function;Lnet/minecraft/world/item/Item$Properties;)Lnet/minecraft/world/item/Item;", ordinal = 0 ), slice = @Slice(from =
    @At(value = "FIELD", target = "Lnet/minecraft/references/ItemIds;BOW:Lnet/minecraft/resources/ResourceKey;", opcode = Opcodes.GETSTATIC), to =
    @At(value = "FIELD",target = "Lnet/minecraft/world/item/Items;BOW:Lnet/minecraft/world/item/Item;", opcode = Opcodes.PUTSTATIC)), index = 2)
    private static Item.Properties repairableBow(Item.Properties properties) {
        return properties.repairable(ModTags.STRING);}

    @ModifyArg(method="<clinit>", at = @At( value = "INVOKE", target = "Lnet/minecraft/world/item/Items;registerItem(Lnet/minecraft/resources/ResourceKey;Ljava/util/function/Function;Lnet/minecraft/world/item/Item$Properties;)Lnet/minecraft/world/item/Item;", ordinal = 0 ), slice = @Slice(from =
    @At(value = "FIELD", target = "Lnet/minecraft/references/ItemIds;CROSSBOW:Lnet/minecraft/resources/ResourceKey;", opcode = Opcodes.GETSTATIC), to =
    @At(value = "FIELD",target = "Lnet/minecraft/world/item/Items;CROSSBOW:Lnet/minecraft/world/item/Item;", opcode = Opcodes.PUTSTATIC)), index = 2)
    private static Item.Properties repairableCrossBow(Item.Properties properties) {
        return properties.repairable(ModTags.STRING);}

    @ModifyArg(method="<clinit>", at = @At( value = "INVOKE", target = "Lnet/minecraft/world/item/Items;registerItem(Lnet/minecraft/resources/ResourceKey;Ljava/util/function/Function;Lnet/minecraft/world/item/Item$Properties;)Lnet/minecraft/world/item/Item;", ordinal = 0 ), slice = @Slice(from =
    @At(value = "FIELD", target = "Lnet/minecraft/references/ItemIds;FISHING_ROD:Lnet/minecraft/resources/ResourceKey;", opcode = Opcodes.GETSTATIC), to =
    @At(value = "FIELD",target = "Lnet/minecraft/world/item/Items;FISHING_ROD:Lnet/minecraft/world/item/Item;", opcode = Opcodes.PUTSTATIC)), index = 2)
    private static Item.Properties repairableFishingRod(Item.Properties properties) {
        return properties.repairable(ModTags.STRING);}

    @ModifyArg(method="<clinit>", at = @At( value = "INVOKE", target = "Lnet/minecraft/world/item/Items;registerItem(Lnet/minecraft/resources/ResourceKey;Lnet/minecraft/world/item/Item$Properties;)Lnet/minecraft/world/item/Item;", ordinal = 0), slice = @Slice(from =
    @At(value = "FIELD", target = "Lnet/minecraft/references/ItemIds;SPIDER_EYE:Lnet/minecraft/resources/ResourceKey;", opcode = Opcodes.GETSTATIC), to =
    @At(value = "FIELD",target = "Lnet/minecraft/world/item/Items;SPIDER_EYE:Lnet/minecraft/world/item/Item;", opcode = Opcodes.PUTSTATIC)), index = 1)
    private static Item.Properties spiderEyeBait(Item.Properties properties) {
        return properties.component(ItemRegistry.BAIT_POWER, new BaitComponent(1));}

    @WrapOperation(method="<clinit>", at = @At( value = "INVOKE", target = "Lnet/minecraft/world/item/Items;registerItem(Lnet/minecraft/resources/ResourceKey;)Lnet/minecraft/world/item/Item;", ordinal = 0), slice = @Slice(from =
    @At(value = "FIELD", target = "Lnet/minecraft/references/ItemIds;FERMENTED_SPIDER_EYE:Lnet/minecraft/resources/ResourceKey;", opcode = Opcodes.GETSTATIC), to =
    @At(value = "FIELD",target = "Lnet/minecraft/world/item/Items;FERMENTED_SPIDER_EYE:Lnet/minecraft/world/item/Item;", opcode = Opcodes.PUTSTATIC)))
    private static Item fermentedSpiderEyeBait(ResourceKey<Item> id, Operation<Item> original) {
        return registerItem(id, new Item.Properties().component(ItemRegistry.BAIT_POWER, new BaitComponent(2)));}

    @ModifyArg(method="<clinit>", at = @At( value = "INVOKE", target = "Lnet/minecraft/world/item/Items;registerItem(Lnet/minecraft/resources/ResourceKey;Ljava/util/function/Function;Lnet/minecraft/world/item/Item$Properties;)Lnet/minecraft/world/item/Item;", ordinal = 0), slice = @Slice(from =
    @At(value = "FIELD", target = "Lnet/minecraft/references/ItemIds;FIREWORK_ROCKET:Lnet/minecraft/resources/ResourceKey;", opcode = Opcodes.GETSTATIC), to =
    @At(value = "FIELD",target = "Lnet/minecraft/world/item/Items;FIREWORK_ROCKET:Lnet/minecraft/world/item/Item;", opcode = Opcodes.PUTSTATIC)), index = 2)
    private static Item.Properties fireWorkCooldown(Item.Properties properties) {
        return properties.useCooldown(5);}

    @WrapOperation(method="<clinit>", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/Items;registerItem(Lnet/minecraft/resources/ResourceKey;)Lnet/minecraft/world/item/Item;"), slice = @Slice( from =
    @At(value = "FIELD", target = "Lnet/minecraft/references/ItemIds;COAL:Lnet/minecraft/resources/ResourceKey;", opcode = Opcodes.GETSTATIC), to =
    @At(value = "FIELD",target = "Lnet/minecraft/world/item/Items;COAL:Lnet/minecraft/world/item/Item;", opcode = Opcodes.PUTSTATIC)))
    private static Item coalTrimMaterial(ResourceKey<Item> id, Operation<Item> original) {
        return registerItem(id, new Item.Properties().trimMaterial(TrimMaterialsRegistry.COAL));}

    @ModifyArgs(method="<clinit>", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/Item$Properties;spear(Lnet/minecraft/world/item/ToolMaterial;FFFFFFFFF)Lnet/minecraft/world/item/Item$Properties;"))
    private static void holdSpearsOutForever(Args args) {
        args.set(4, (float)args.get(4)+32000);
        args.set(6, (float)args.get(6)+32000);
        args.set(8, (float)args.get(8)+32000);
    }

    @WrapOperation(method="<clinit>", at = @At( value = "INVOKE", target = "Lnet/minecraft/world/item/Item$Properties;fireResistant()Lnet/minecraft/world/item/Item$Properties;"))
    private static Item.Properties blastProofNetherite(Item.Properties instance, Operation<Item.Properties> original) {
        return original.call(instance).delayedComponent(DataComponents.DAMAGE_RESISTANT, (context) -> new DamageResistant(context.getOrThrow(DamageTypeTags.IS_EXPLOSION)));}

    @WrapOperation(method="<clinit>", at = @At( value = "INVOKE", target = "Lnet/minecraft/world/item/Items;registerItem(Lnet/minecraft/resources/ResourceKey;)Lnet/minecraft/world/item/Item;", ordinal = 0 ), slice = @Slice(from =
    @At(value = "FIELD", target = "Lnet/minecraft/references/ItemIds;BLAZE_ROD:Lnet/minecraft/resources/ResourceKey;", opcode = Opcodes.GETSTATIC), to =
    @At(value = "FIELD",target = "Lnet/minecraft/world/item/Items;BLAZE_ROD:Lnet/minecraft/world/item/Item;", opcode = Opcodes.PUTSTATIC)))
    private static Item fireProofBlazeRod(ResourceKey<Item> id, Operation<Item> original) {
        return registerItem(id, new Item.Properties().fireResistant());}
}
